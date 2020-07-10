package com.selfStudy.quicksaleevent.rabbitmq;

import com.selfStudy.quicksaleevent.utils.BeanStringConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQConfig.class);

    AmqpTemplate amqpTemplate;

    public MQSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }


    public void send(Object message) {
        String msg = BeanStringConvert.beanToString(message);
        log.info("send message:" + message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }
}
