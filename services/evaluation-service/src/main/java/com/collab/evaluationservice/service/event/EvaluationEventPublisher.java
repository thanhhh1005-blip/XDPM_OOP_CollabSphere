package com.collab.evaluationservice.service.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EvaluationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EvaluationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEvaluationCreated(Long evaluationId) {
        rabbitTemplate.convertAndSend(
                "evaluation.exchange",
                "evaluation.created",
                evaluationId
        );
    }
}
