package com.selfStudy.quicksaleevent.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQConfig.class);

    /**
     * Direct Mode
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("received messages:" + message);
    }

    /**
     * Topic Mode or Fanout Mode
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("received topic queue 1 messages:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("received topic queue 2 messages:" + message);
    }

//    /**
//     * Headers Mode
//     */
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeaders(byte[] message) {
//        log.info("received header queue messages:" + new String(message));
//    }
}
