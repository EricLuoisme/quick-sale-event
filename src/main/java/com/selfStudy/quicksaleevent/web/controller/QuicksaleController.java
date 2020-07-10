package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.rabbitmq.MQSender;
import com.selfStudy.quicksaleevent.rabbitmq.QuickSaleMsg;
import com.selfStudy.quicksaleevent.redis.GoodsKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private Logger log = LoggerFactory.getLogger(QuicksaleController.class);

    private Map<Long, Boolean> localOverMap = new HashMap<>();

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
                localOverMap.put(good.getId(), false); // false means : this good can still be purchasing
            }
        }

    }

    @RequestMapping(value = "/do_quicksale", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> quicksale(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {

        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 2. use memory temp to reduce times of visiting Redis
        Boolean stopOrNot = localOverMap.get(goodsId);
        if (stopOrNot)
            return Result.error(CodeMsg.EVENT_STORAGE_EMPTY);

        // 3. reduce the stock
        long stock = redisService.decr(GoodsKey.getQuickSalesGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.EVENT_STORAGE_EMPTY);
        }

        // 4. adding the order into Rabbitmq
        QuickSaleMsg msgObj = new QuickSaleMsg();
        msgObj.setUser(user);
        msgObj.setGoodsId(goodsId);
        mqSender.sendQuickSaleMsg(msgObj);

        return Result.success(0);
    }

    /**
     * order id : successfully purchase a quick sale good
     * -1 : stock is empty
     * 0 : be queuing
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> quicksaleResult(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        long result = quickSaleService.getQuicksaleResult(user.getId(), goodsId);
        return Result.success(result);
    }
}
