package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.OrderDao;
import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.redis.OrderKey;
import com.selfStudy.quicksaleevent.redis.RedisService;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    OrderDao orderDao;

    RedisService redisService;

    public OrderService(OrderDao orderDao, RedisService redisService) {
        this.orderDao = orderDao;
        this.redisService = redisService;
    }

    public QuickSaleOrder getQuickSaleOrderByUserIdGoodsId(long userId, long goodsId) {
        // checking cache instead of checking the database
        return redisService.get(OrderKey.getQuickSaleOrderByUidGid, "" + userId + "_" + goodsId, QuickSaleOrder.class);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public OrderInfo createOrder(QuickSaleUser user, GoodsVo goods) {

        // create an order
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getQuicksalePrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0); // 0: not paid order
        orderInfo.setUserId(user.getId());

        // creat a quick sale order
        orderDao.insert(orderInfo);
        QuickSaleOrder quicksaleOrder = new QuickSaleOrder();
        quicksaleOrder.setGoodsId(goods.getId());
        quicksaleOrder.setOrderId(orderInfo.getGoodsId());
        quicksaleOrder.setUserId(user.getId());
        orderDao.insertQuickSaleOrder(quicksaleOrder);

        // set key into the Redis cache
        redisService.set(OrderKey.getQuickSaleOrderByUidGid, "" + user.getId() + "_" + goods.getId(), quicksaleOrder);

        return orderInfo;
    }
}
