package com.collab.communicationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Đặt tên cho các thành phần
    public static final String CHAT_QUEUE = "chat_queue";       // Tên hòm thư
    public static final String CHAT_EXCHANGE = "chat_exchange"; // Tên bưu cục
    public static final String CHAT_ROUTING_KEY = "chat_key";   // Mã định tuyến

    // 1. Tạo Hòm thư (Queue)
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE, true); // true = Bền vững (không mất khi tắt máy)
    }

    // 2. Tạo Bưu cục (Exchange)
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    // 3. Nối Hòm thư vào Bưu cục
    @Bean
    public Binding binding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(CHAT_ROUTING_KEY);
    }

    // 4. Bộ chuyển đổi tin nhắn sang JSON (Quan trọng)
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}