package com.collabsphere.aiservice.controller;

import com.collabsphere.aiservice.entity.AiGenerationLog;
import com.collabsphere.aiservice.repository.AiGenerationLogRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/history") 
public class HistoryController {

    @Autowired
    private AiGenerationLogRepository aiLogRepository; // 3. Tiêm Repository mới vào

    // API: Lấy toàn bộ danh sách lịch sử từ bảng ai_generation_logs
    @GetMapping
    public List<AiGenerationLog> getAllHistory() {
        return aiLogRepository.findAll(); 
    }
}