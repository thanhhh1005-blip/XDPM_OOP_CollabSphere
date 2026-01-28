package com.collabsphere.identity.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String USER_EXCHANGE = "user_exchange";
    public static final String USER_QUEUE = "user_workspace_queue";
    public static final String USER_ROUTING_KEY = "user_updated";

    // 1. Tạo Exchange (Bưu điện)
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    // 2. Tạo Queue (Thùng thư) - Lưu ý dùng org.springframework.amqp.core.Queue
    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true); // true = bền vững, không mất khi restart
    }


    @Bean
    public Binding binding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(USER_ROUTING_KEY);
    }

    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}