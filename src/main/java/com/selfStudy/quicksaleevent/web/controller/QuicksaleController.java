package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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


    @RequestMapping("/do_quicksale")
    public String list(Model model, QuickSaleUser user, @RequestParam("goodsId") long goodsId) {

        // 1. check user status
        if (user == null)
            return "login"; // user must be login then can purchase an item

        // 2. check storage of this item
        GoodsVO goodsVO = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVO.getStockCount(); // check stock for quick sale event
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.EVENT_STORAGE_EMPTY.getMsg());
            return "quicksale_fail";
        }

        // 3. check whether same user buy more than one spick item in same event
        QuickSaleOrder order = orderService.getSpikeOrderByUserIdGoodsId(user.getId(), goodsId);
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
