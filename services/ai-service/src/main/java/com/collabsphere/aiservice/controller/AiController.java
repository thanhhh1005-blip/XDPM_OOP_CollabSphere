package com.collabsphere.aiservice.controller;

import com.collabsphere.aiservice.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final GeminiService geminiService;

    @Autowired
    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        // 👇 SỬA Ở ĐÂY: Đổi "message" thành "question" cho khớp với file test-api.http
        String userQuestion = request.get("question"); 
        
        // Gọi Service
        String aiReply = geminiService.callGemini(userQuestion);
        
        return Map.of("reply", aiReply);
    }
}