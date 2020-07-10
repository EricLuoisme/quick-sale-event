package com.selfStudy.quicksaleevent.rabbitmq;

import com.selfStudy.quicksaleevent.utils.BeanStringConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQConfig.class);

    AmqpTemplate amqpTemplate;

    public MQSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendQuickSaleMsg(QuickSaleMsg msgObj) {
        /**
         * Using direct mode
         */
        String msg = BeanStringConvert.beanToString(msgObj);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUICKSALE_QUEUE, msg);
    }


//    /**
//     * Direct Mode
//     */
//    public void send(Object message) {
//        String msg = BeanStringConvert.beanToString(message);
//        log.info("send message:" + message);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//    /**
//     * Topic Mode
//     */
//    public void sendTopic(Object message) {
//        String msg = BeanStringConvert.beanToString(message);
//        log.info("send topic message:" + message);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
//    }
//
//    /**
//     * Fanout Mode (broadcast)
//     */
//    public void sendFanout(Object message) {
//        String msg = BeanStringConvert.beanToString(message);
//        log.info("send fanout message:" + message);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
//    }
//
//    /**
//     * Headers Mode (broadcast)
//     */
//    public void sendHeaders(Object message) {
//        String msg = BeanStringConvert.beanToString(message);
//        log.info("send headers message:" + message);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1", "value1");
//        properties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//    }

}
