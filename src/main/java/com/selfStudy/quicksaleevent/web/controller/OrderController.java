package com.selfStudy.quicksaleevent.web.controller;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleUserService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.vo.OrderDetailVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    /**
     * For pressure testing on getting user information
     */

    RedisService redisService;

    QuickSaleUserService quickSaleUserService;

    OrderService orderService;

    GoodsService goodsService;

    public OrderController(RedisService redisService, QuickSaleUserService quickSaleUserService,
                           OrderService orderService, GoodsService goodsService) {
        this.redisService = redisService;
        this.quickSaleUserService = quickSaleUserService;
        this.orderService = orderService;
        this.goodsService = goodsService;
    }

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, QuickSaleUser user, @RequestParam("orderId") long orderId) {
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null)
            return Result.error(CodeMsg.ORDER_NOT_EXIST);

        Long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }


}
