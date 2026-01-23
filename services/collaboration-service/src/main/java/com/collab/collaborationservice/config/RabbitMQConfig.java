package com.collab.collaborationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "whiteboard_save_queue";
    public static final String EXCHANGE_NAME = "collab_exchange";
    public static final String ROUTING_KEY = "whiteboard_save";

    // 1. Tạo Hàng đợi (Queue)
    @Bean
    public Queue whiteboardQueue() {
        return new Queue(QUEUE_NAME, true); // true = bền vững (không mất khi restart)
    }

    // 2. Tạo Bưu điện (Exchange)
    @Bean
    public TopicExchange collabExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 3. Nối Hàng đợi vào Bưu điện
    @Bean
    public Binding binding(Queue whiteboardQueue, TopicExchange collabExchange) {
        return BindingBuilder.bind(whiteboardQueue).to(collabExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}