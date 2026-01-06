package com.collab.collaborationservice.event;

import com.collab.collaborationservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollaborationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishCollaborationCreated(CollaborationCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLLAB_EXCHANGE,
                "collaboration.created",
                event
        );
    }

    public void publishMemberAdded(MemberAddedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLLAB_EXCHANGE,
                "collaboration.member.added",
                event
        );
    }

    public void publishResourceShared(ResourceSharedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLLAB_EXCHANGE,
                "collaboration.resource.shared",
                event
        );
    }
}
