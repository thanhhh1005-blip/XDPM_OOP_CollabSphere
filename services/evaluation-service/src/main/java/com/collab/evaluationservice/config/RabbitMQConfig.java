package com.collab.evaluationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "evaluation.exchange";
    public static final String QUEUE_NAME = "evaluation.created.queue";
    public static final String ROUTING_KEY = "evaluation.created";

    @Bean
    public DirectExchange evaluationExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue evaluationCreatedQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding evaluationBinding() {
        return BindingBuilder
                .bind(evaluationCreatedQueue())
                .to(evaluationExchange())
                .with(ROUTING_KEY);
    }
}
