package com.collab.communicationservice.controller;

import com.collab.shared.dto.ApiResponse; // Import từ module shared
import com.collab.communicationservice.entity.ChatMessage;
import com.collab.communicationservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller; // Đổi thành @Controller để xử lý WebSocket
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller 
public class ChatController {

    @Autowired
    private ChatMessageRepository chatRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Xử lý khi có người gửi tin nhắn lên
    @MessageMapping("/chat.sendMessage") 
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatRepo.save(chatMessage); // Lưu vào DB
        
        // Gửi tin nhắn này đến tất cả mọi người trong phòng
        // Ví dụ: /topic/room/1
        simpMessagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
        
        return chatMessage;
    }
    
    // API lấy lịch sử chat\
    @ResponseBody
    @GetMapping("/api/chat/history/{roomId}")
    public Iterable<ChatMessage> getHistory(@PathVariable Long roomId) {
        // Em nhớ thêm hàm findByRoomId trong Repository nhé
        return chatRepo.findAll(); // Tạm thời lấy hết, sửa sau
    }
}