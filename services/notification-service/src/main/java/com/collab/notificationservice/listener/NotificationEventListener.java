package com.collab.notificationservice.listener;

import com.collab.notificationservice.dto.NotificationEventDTO;
import com.collab.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void listen(NotificationEventDTO event) {
        notificationService.handleNotification(event);
    }
}
