package com.collab.communicationservice.service;

import com.collab.communicationservice.config.RabbitMQConfig;
import com.collab.communicationservice.entity.ChatMessage;
import com.collab.communicationservice.repository.ChatMessageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatConsumer {

    @Autowired
    private ChatMessageRepository chatRepo;

    // Lắng nghe hòm thư "chat_queue"
    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void saveMessageToDB(ChatMessage message) {
        try {
            // Lưu tin nhắn vào MySQL
            chatRepo.save(message);
            System.out.println("✅ RabbitMQ đã lưu tin nhắn của: " + message.getSenderName());
        } catch (Exception e) {
            System.err.println("❌ Lỗi lưu tin nhắn: " + e.getMessage());
        }
    }
}