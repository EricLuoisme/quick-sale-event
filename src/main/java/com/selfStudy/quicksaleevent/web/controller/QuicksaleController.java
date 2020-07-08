package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/quicksale")
public class QuicksaleController {

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    GoodsService goodsService;

    OrderService orderService;

    QuickSaleService quickSaleService;

    public QuicksaleController(RedisService redisService, QuickSaleUserService quickSaleUserService, GoodsService goodsService, OrderService orderService, QuickSaleService quickSaleService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.quickSaleService = quickSaleService;
    }


    @RequestMapping(value = "/do_quicksale", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> quicksale(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {

        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 2. check storage of this item
        GoodsVo goodsVO = goodsService.getGoodsVoByGoodsId(goodsId); // If a user sent req 1 and req 2
        int stock = goodsVO.getStockCount(); // check stock for quick sale event
        if (stock <= 0) {
            return Result.error(CodeMsg.EVENT_STORAGE_EMPTY);
        }

        // 3. check whether same user buy more than one spick item in same event (Just check the Cache)
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.CANNOT_BUY_TWICE);
        }

        // Spike Sale Operations
        OrderInfo orderInfo = quickSaleService.doSale(user, goodsVO);
        return Result.success(orderInfo);
    }

    @RequestMapping("/do_quicksaleOld")
    public String listOld(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {

        model.addAttribute("user", user);

        // 1. check user status
        if (user == null)
            return "login"; // user must be login then can purchase an item

        // 2. check storage of this item
        GoodsVo goodsVO = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVO.getStockCount(); // check stock for quick sale event
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.EVENT_STORAGE_EMPTY.getMsg());
            return "quicksale_fail";
        }

        // 3. check whether same user buy more than one spick item in same event
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.CANNOT_BUY_TWICE.getMsg());
            return "quicksale_fail";
        }

        // Spike Sale Operations
        OrderInfo orderInfo = quickSaleService.doSale(user, goodsVO);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVO);
        return "order_detail";
    }

}
