package com.selfStudy.quicksaleevent.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String HEADERS_QUEUE = "header.queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "FanoutExchange";
        public static final String HEADERS_EXCHANGE = "FanoutExchange";

    /**
     * Direct Mode
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Direct Mode or Topic Mode all need this Queue
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }

    /**
     * Topic Mode
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
        // routing key is a matching pattern, where # means 0 or more, * means 1
    }

    /**
     * Fanout Mode (broadcast)
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    /**
     * Header Mode
     */
//    @Bean
//    public HeadersExchange headersExchange() {
//        return new HeadersExchange(HEADERS_EXCHANGE);
//    }
//
//    @Bean
//    public Queue headersQueue() {
//        return new Queue(HEADERS_QUEUE, true);
//    }
//
//    @Bean
//    public Binding headersBinding() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("header1", "value1");
//        map.put("header2", "value2");
//        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
//        // only sending the data when the map satisfied the value we set
//    }

}
