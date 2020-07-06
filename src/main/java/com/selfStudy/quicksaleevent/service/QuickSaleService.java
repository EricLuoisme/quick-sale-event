package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.domain.model.Goods;
import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.vo.GoodsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuickSaleService {

    GoodsService goodsService;

    OrderService orderService;

    public QuickSaleService(GoodsService goodsService, OrderService orderService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
    }

    @Transactional
    public OrderInfo doSale(QuickSaleUser user, GoodsVO goods) {
        goodsService.reduceStock(goods); // reduce the stock
        return orderService.createOrder(user, goods); // create a order
    }
}
