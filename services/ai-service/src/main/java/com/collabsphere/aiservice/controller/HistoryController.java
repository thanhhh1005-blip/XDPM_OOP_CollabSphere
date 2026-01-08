package com.collabsphere.aiservice.controller;

import com.collabsphere.aiservice.entity.AiGenerationLog; // 1. Import Entity mới
import com.collabsphere.aiservice.repository.AiGenerationLogRepository; // 2. Import Repository mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/history") // Giữ nguyên đường dẫn này để khớp với Gateway"/api/ai"
public class HistoryController {

    @Autowired
    private AiGenerationLogRepository aiLogRepository; // 3. Tiêm Repository mới vào

    // API: Lấy toàn bộ danh sách lịch sử từ bảng ai_generation_logs
    @GetMapping
    public List<AiGenerationLog> getAllHistory() {
        // Hàm này sẽ trả về JSON gồm: id, question, answer, timestamp
        // (Do ta đã map tên biến trong Entity AiGenerationLog rồi)
        return aiLogRepository.findAll(); 
    }
}