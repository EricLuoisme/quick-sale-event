package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.access.AccessLimit;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.rabbitmq.MQSender;
import com.selfStudy.quicksaleevent.rabbitmq.QuickSaleMsg;
import com.selfStudy.quicksaleevent.redis.*;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quicksale")
public class QuicksaleController implements InitializingBean {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    GoodsService goodsService;

    OrderService orderService;

    QuickSaleService quickSaleService;

    MQSender mqSender;

    private static HashMap<Long, Integer> stockMap =  new HashMap<>(); // store original stock before quick sale
    private Map<Long, Boolean> localOverMap = new HashMap<>(); // if a quick sale good's is out of stock, we can just check this boolean result

    public QuicksaleController(RedisService redisService, QuickSaleUserService quickSaleUserService,
                               GoodsService goodsService, OrderService orderService,
                               QuickSaleService quickSaleService, MQSender mqSender) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.quickSaleService = quickSaleService;
        this.mqSender = mqSender;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * initialization: add goods' stock in database into Redis
         */
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList != null) {
            for (GoodsVo good : goodsVoList) {
                redisService.set(GoodsKey.getQuickSalesGoodsStock, "" + good.getId(), good.getStockCount()); // save stock number in Redis
                stockMap.put(good.getId(), good.getStockCount());
                localOverMap.put(good.getId(), false); // false means : this good can still be purchasing
            }
        }
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getQuicksaleVerifyCod(HttpServletResponse response, QuickSaleUser user,
                                                @RequestParam("goodsId") long goodsId) {
        /**
         * draw the verify code
         */
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = quickSaleService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null; // the data is sent by output stream
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.QUICK_SALE_FAIL);
        }
    }

    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getQuicksalePath(HttpServletRequest request, QuickSaleUser user,
                                           @RequestParam("goodsId") long goodsId,
                                           @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        // here default value is for testing 'reach visit limit'
        /**
         * For getting path before the user purchase goods
         */
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 1. do verify code validation
        boolean check = quickSaleService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 2. do path validation
        String path = quickSaleService.createQuicksalePath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/{path}/do_quicksale", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> quicksale(Model model, QuickSaleUser user,
                                     @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {
        /**
         * Original QPS : 127
         * Improved QPS : 540
         */
        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 2. verirfiy path is valid or not
        boolean check = quickSaleService.checkPath(user, goodsId, path);
        if (!check)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);

        // 3. use memory temp to reduce times of visiting Redis (if stock is empty, stopOrNot should be true)
        Boolean stopOrNot = localOverMap.get(goodsId);
        if (stopOrNot)
            return Result.error(CodeMsg.EVENT_STORAGE_EMPTY);

        // 4. reduce the stock
        long stock = redisService.decr(GoodsKey.getQuickSalesGoodsStock, "" + goodsId); // reduce the stock for every valid visit
        if (stock < 0) { // then check whether it's valid or not
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.EVENT_STORAGE_EMPTY);
        }

        // 5. checking whether this user repeat buy same good more than once
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.CANNOT_BUY_TWICE);
        }

        // 6. adding the order into Rabbitmq
        QuickSaleMsg msgObj = new QuickSaleMsg();
        msgObj.setUser(user);
        msgObj.setGoodsId(goodsId);
        mqSender.sendQuickSaleMsg(msgObj);

        return Result.success(0);
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> quicksaleResult(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {
        /**
         * order id : successfully purchase a quick sale good
         * -1 : stock is empty
         * 0 : be queuing
         */
        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        long result = quickSaleService.getQuicksaleResult(user.getId(), goodsId);
        return Result.success(result);
    }

    public static int getGoodsStockOriginal(long goodsId) {
        /**
         * Get good's original stock
         */
        Integer stock = stockMap.get(goodsId);
        if(stock == null) {
            return 0;
        }
        return stock;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        /**
         * For reset the test
         */
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getQuickSalesGoodsStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getQuickSaleOrderByUidGid);
        redisService.delete(QuickSaleKey.isOutOfStock);
        quickSaleService.reset(goodsList);
        return Result.success(true);
    }
}
