package com.collab.resourceservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    // Exchange phải trùng RabbitMQConfig
    private static final String EXCHANGE = "resource.exchange";
    public void publish(String routingKey, ResourceEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, event);
    }
}
