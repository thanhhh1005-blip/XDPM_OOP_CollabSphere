package com.collabsphere.aiservice.controller;

import com.collabsphere.aiservice.dto.ProjectPlanResponse;
import com.collabsphere.aiservice.dto.request.MilestoneRequest;
import com.collabsphere.aiservice.entity.AiGenerationLog; // 👈 Import Entity mới
import com.collabsphere.aiservice.repository.AiGenerationLogRepository; // 👈 Import Repo mới
import com.collabsphere.aiservice.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper; // 👈 Dùng để xử lý JSON
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;
    private final AiGenerationLogRepository aiLogRepository; 
    private final ObjectMapper objectMapper;

    @Autowired
    public AiController(GeminiService geminiService, AiGenerationLogRepository aiLogRepository, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.aiLogRepository = aiLogRepository; 
        this.objectMapper = objectMapper;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userQuestion = request.get("question");
        String aiReply = geminiService.callGemini(userQuestion);
        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    @PostMapping("/generate-milestones")
    public ResponseEntity<ProjectPlanResponse> generateMilestones(@RequestBody MilestoneRequest request) {
        // Chỉ gọi AI và trả về kết quả để hiển thị, KHÔNG LƯU DATABASE ở đây
        String syllabus = request.getSyllabus();
        ProjectPlanResponse planResponse = geminiService.generateProjectPlan(syllabus);
        return ResponseEntity.ok(planResponse);
    }

    @PostMapping("/save-log")
    public ResponseEntity<Map<String, String>> saveLog(@RequestBody Map<String, Object> requestBody) {
        try {
           
            String syllabus = (String) requestBody.get("syllabus");
            Object resultObj = requestBody.get("jsonResult"); 

            
            String jsonResultString = "";
            if (resultObj instanceof String) {
                jsonResultString = (String) resultObj;
            } else {
               
                jsonResultString = objectMapper.writeValueAsString(resultObj);
            }

           
            AiGenerationLog log = new AiGenerationLog(syllabus, jsonResultString);
            aiLogRepository.save(log);

            return ResponseEntity.ok(Map.of("message", "✅ Đã lưu thành công! ID log: " + log.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "❌ Lỗi lưu database: " + e.getMessage()));
        }
    }
}