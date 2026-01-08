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
    private final AiGenerationLogRepository aiLogRepository; // 1. Khai báo Repository
    private final ObjectMapper objectMapper; // Dùng để chuyển Object -> JSON String

    @Autowired
    public AiController(GeminiService geminiService, AiGenerationLogRepository aiLogRepository, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.aiLogRepository = aiLogRepository; // 2. Inject Repository
        this.objectMapper = objectMapper;
    }

    // ==========================================
    // 1. API Chat thường
    // ==========================================
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userQuestion = request.get("question");
        String aiReply = geminiService.callGemini(userQuestion);
        return ResponseEntity.ok(Map.of("reply", aiReply));
    }

    // ==========================================
    // 2. API XEM TRƯỚC (Generate - Preview Only)
    // ==========================================
    @PostMapping("/generate-milestones")
    public ResponseEntity<ProjectPlanResponse> generateMilestones(@RequestBody MilestoneRequest request) {
        // Chỉ gọi AI và trả về kết quả để hiển thị, KHÔNG LƯU DATABASE ở đây
        String syllabus = request.getSyllabus();
        ProjectPlanResponse planResponse = geminiService.generateProjectPlan(syllabus);
        return ResponseEntity.ok(planResponse);
    }

    // ==========================================
    // 3. API LƯU DATABASE (Save - Commit) 🆕
    // ==========================================
    @PostMapping("/save-log")
    public ResponseEntity<Map<String, String>> saveLog(@RequestBody Map<String, Object> requestBody) {
        try {
            // Lấy dữ liệu từ Frontend gửi xuống
            String syllabus = (String) requestBody.get("syllabus");
            Object resultObj = requestBody.get("jsonResult"); 

            // Chuyển kết quả (Object/JSON) thành String để lưu vào cột TEXT trong MySQL
            String jsonResultString = "";
            if (resultObj instanceof String) {
                jsonResultString = (String) resultObj;
            } else {
                // Nếu frontend gửi nguyên object, ta chuyển nó thành string
                jsonResultString = objectMapper.writeValueAsString(resultObj);
            }

            // Tạo Entity và Lưu
            AiGenerationLog log = new AiGenerationLog(syllabus, jsonResultString);
            aiLogRepository.save(log);

            return ResponseEntity.ok(Map.of("message", "✅ Đã lưu thành công! ID log: " + log.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "❌ Lỗi lưu database: " + e.getMessage()));
        }
    }
}