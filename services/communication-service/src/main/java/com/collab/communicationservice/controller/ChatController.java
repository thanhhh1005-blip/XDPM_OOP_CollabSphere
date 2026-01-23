package com.collab.communicationservice.controller;

import com.collab.shared.dto.ApiResponse; // Import từ module shared
import com.collab.communicationservice.config.RabbitMQConfig;
import com.collab.communicationservice.entity.ChatMessage;
import com.collab.communicationservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller; // Đổi thành @Controller để xử lý WebSocket
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller 
public class ChatController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ChatMessageRepository chatRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Xử lý khi có người gửi tin nhắn lên
  @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());

        // 1. Gửi NGAY LẬP TỨC cho người nhận (qua WebSocket) để giao diện mượt
        simpMessagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);

        // 2. Gửi vào RabbitMQ để lưu trữ (Không lưu DB ở đây nữa)
        // Code chạy cực nhanh vì không phải chờ DB
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.CHAT_EXCHANGE, 
            RabbitMQConfig.CHAT_ROUTING_KEY, 
            chatMessage
        );
        
        System.out.println(">>> Đã gửi tin nhắn vào RabbitMQ!");
    }

    // API lấy lịch sử chat
    @ResponseBody
    @GetMapping("/api/chat/history/{roomId}")
    public List<ChatMessage> getHistory(@PathVariable("roomId") String roomId) {
        return chatRepo.findByRoomId(roomId);
    }
}