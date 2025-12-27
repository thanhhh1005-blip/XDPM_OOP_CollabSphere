package com.collab.resourceservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ===================== CONSTANTS =====================
    public static final String RESOURCE_EXCHANGE = "resource.exchange";
    public static final String RESOURCE_QUEUE = "resource.queue";
    public static final String RESOURCE_ROUTING_KEY = "resource.*";

    // ===================== EXCHANGE =====================
    @Bean
    public TopicExchange resourceExchange() {
        return new TopicExchange(RESOURCE_EXCHANGE);
    }

    // ===================== QUEUE =====================
    @Bean
    public Queue resourceQueue() {
        return new Queue(RESOURCE_QUEUE, true);
    }

    // ===================== BINDING =====================
    @Bean
    public Binding resourceBinding(
            Queue resourceQueue,
            TopicExchange resourceExchange
    ) {
        return BindingBuilder
                .bind(resourceQueue)
                .to(resourceExchange)
                .with(RESOURCE_ROUTING_KEY);
    }
}
