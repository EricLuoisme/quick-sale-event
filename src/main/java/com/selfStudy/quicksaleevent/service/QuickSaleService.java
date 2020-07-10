package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.QuickSaleKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuickSaleService {

    GoodsService goodsService;

    OrderService orderService;

    RedisService redisService;

    public QuickSaleService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
    }


    @Transactional
    public OrderInfo doSale(QuickSaleUser user, GoodsVo goods) {
        /**
         * simulating the purchase operation
         */
        boolean successOrNot = goodsService.reduceStock(goods); // reduce the stock
        if (successOrNot) {
            return orderService.createOrder(user, goods); // create a order
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }


    public long getQuicksaleResult(Long userId, long goodsId) {
        /**
         * return purchase result
         */
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) { // successfully purchased goods
            return order.getOrderId();
        } else {
            boolean outOfStock = getGoodsOver(goodsId);
            if (outOfStock)
                return -1;
            else
                return 0;
        }
    }


    private void setGoodsOver(Long goodsId) {
        /**
         * if good is out of stock, we set an unexpired key in Redis to represent it
         */
        redisService.set(QuickSaleKey.isOutOfStock, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exist(QuickSaleKey.isOutOfStock, "" + goodsId);
    }
}
