package com.selfStudy.quicksaleevent.rabbitmq;

import com.selfStudy.quicksaleevent.domain.model.Goods;
import com.selfStudy.quicksaleevent.domain.model.OrderInfo;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleOrder;
import com.selfStudy.quicksaleevent.domain.model.QuickSaleUser;
import com.selfStudy.quicksaleevent.service.GoodsService;
import com.selfStudy.quicksaleevent.service.OrderService;
import com.selfStudy.quicksaleevent.service.QuickSaleService;
import com.selfStudy.quicksaleevent.utils.BeanStringConvert;
import com.selfStudy.quicksaleevent.vo.GoodsVo;
import com.selfStudy.quicksaleevent.web.result.CodeMsg;
import com.selfStudy.quicksaleevent.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    GoodsService goodsService;

    OrderService orderService;

    QuickSaleService quickSaleService;

    private static Logger log = LoggerFactory.getLogger(MQConfig.class);

    public MQReceiver(GoodsService goodsService, OrderService orderService, QuickSaleService quickSaleService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.quickSaleService = quickSaleService;
    }


    @RabbitListener(queues = MQConfig.QUICKSALE_QUEUE)
    public void receive(String message) {

        // 1. get order info from Rabbitmq's polling
        log.info("received messages:" + message);
        QuickSaleMsg quickSaleMsg = BeanStringConvert.stringToBean(message, QuickSaleMsg.class);
        QuickSaleUser user = quickSaleMsg.getUser();
        long goodsId = quickSaleMsg.getGoodsId();

        // 2. check storage
        GoodsVo goodsVO = goodsService.getGoodsVoByGoodsId(goodsId); // If a user sent req 1 and req 2
        int stock = goodsVO.getStockCount(); // check stock for quick sale event
        if (stock <= 0)
            return;

        // 3. need to judge whether use purchase this item twice
        QuickSaleOrder order = orderService.getQuickSaleOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null)
            return;

        // 4. create Quick Sale order
        OrderInfo orderInfo = quickSaleService.doSale(user, goodsVO);
    }


//    /**
//     * Direct Mode
//     */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message) {
//        log.info("received messages:" + message);
//    }
//
//    /**
//     * Topic Mode or Fanout Mode
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message) {
//        log.info("received topic queue 1 messages:" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message) {
//        log.info("received topic queue 2 messages:" + message);
//    }

//    /**
//     * Headers Mode
//     */
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeaders(byte[] message) {
//        log.info("received header queue messages:" + new String(message));
//    }
}
