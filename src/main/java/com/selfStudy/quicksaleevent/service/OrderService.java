package com.selfStudy.quicksaleevent.service;

import com.selfStudy.quicksaleevent.dao.OrderDao;
import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public QuickSaleOrder getSpikeOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderDao.getSpikeOrderByUserIdGoodsId(userId, goodsId);
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
        long orderId = orderDao.insert(orderInfo);
        QuickSaleOrder miaoshaOrder = new QuickSaleOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertQuickSaleOrder(miaoshaOrder);

        return orderInfo;
    }
}
