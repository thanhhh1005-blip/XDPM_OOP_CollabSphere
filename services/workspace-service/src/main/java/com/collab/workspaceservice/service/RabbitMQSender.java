package com.collab.workspaceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailNotification(String toEmail, String subject, String body) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", toEmail);
        message.put("subject", subject);
        message.put("body", body);
        rabbitTemplate.convertAndSend("notification.queue", message);
        System.out.println("🚀 Đã đẩy yêu cầu gửi mail tới RabbitMQ: " + toEmail);
    }
}