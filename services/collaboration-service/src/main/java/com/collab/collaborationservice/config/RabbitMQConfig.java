package com.collab.collaborationservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLLAB_EXCHANGE = "collaboration.exchange";

    @Bean
    public TopicExchange collaborationExchange() {
        return new TopicExchange(COLLAB_EXCHANGE);
    }
}
