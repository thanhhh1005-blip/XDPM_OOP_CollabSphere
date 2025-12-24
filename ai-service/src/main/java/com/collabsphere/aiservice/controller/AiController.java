package com.collabsphere.aiservice.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    // API Test thử: Chat đơn giản
    // URL: http://localhost:8082/ai/chat
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        
        // Tạm thời AI chỉ là con vẹt, nhại lại lời bạn nói
        // (Sau này chúng ta sẽ gắn não thật vào đây)
        String aiReply = "AI Service đã nhận được câu hỏi: '" + userMessage + "'";
        
        return Map.of("reply", aiReply);
    }
}