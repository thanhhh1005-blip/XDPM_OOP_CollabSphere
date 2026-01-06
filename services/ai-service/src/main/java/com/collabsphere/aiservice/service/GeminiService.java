package com.collabsphere.aiservice.service;

import com.collabsphere.aiservice.dto.ProjectPlanResponse;
import com.collabsphere.aiservice.entity.ChatHistory;
import com.collabsphere.aiservice.repository.ChatHistoryRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final ChatHistoryRepository chatHistoryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public GeminiService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // CẤU HÌNH QUAN TRỌNG: Bỏ qua lỗi nếu AI trả về trường thừa
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ============================================================
    // 1. TẠO KẾ HOẠCH DỰ ÁN
    // ============================================================
    public ProjectPlanResponse generateProjectPlan(String userInput) {
        // 🔥 ĐÃ SỬA: Prompt yêu cầu trả về camelCase (projectName) để khớp với Java
        String prompt = """
            Bạn là một Project Manager chuyên nghiệp. Nhiệm vụ: Lập kế hoạch dự án chi tiết.
            
            YÊU CẦU BẮT BUỘC:
            1. Trả về format JSON thuần túy, KHÔNG dùng Markdown (```json).
            2. Sử dụng key dạng camelCase (ví dụ: projectName, phaseName).
            3. Cấu trúc JSON phải chính xác như sau:
            {
              "projectName": "Tên dự án",
              "overview": "Mô tả tổng quan",
              "milestones": [
                {
                  "phaseNumber": 1,
                  "phaseName": "Tên giai đoạn",
                  "duration": "Thời gian",
                  "description": "Mô tả chi tiết",
                  "tasks": ["Task 1", "Task 2"],
                  "deliverables": "Sản phẩm bàn giao"
                }
              ]
            }
            
            Đề bài: "%s"
            """.formatted(userInput);

        // Gọi AI
        String rawResult = callGemini(prompt);

        // Kiểm tra lỗi kết nối
        if (rawResult.startsWith("LỖI:")) {
            ProjectPlanResponse err = new ProjectPlanResponse();
            err.setProjectName("⚠️ LỖI KẾT NỐI AI");
            err.setOverview(rawResult);
            return err;
        }

        // Làm sạch kết quả
        String cleanJson = cleanJsonResult(rawResult);
        System.out.println(">>> Clean JSON: " + cleanJson);

        try {
            return objectMapper.readValue(cleanJson, ProjectPlanResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            ProjectPlanResponse errorResponse = new ProjectPlanResponse();
            errorResponse.setProjectName("Lỗi xử lý dữ liệu");
            errorResponse.setOverview("AI trả về sai định dạng. Raw: " + cleanJson);
            return errorResponse;
        }
    }

    // ============================================================
    // 2. GỌI API GEMINI
    // ============================================================
    public String callGemini(String question) {
        if (question == null || question.trim().isEmpty()) return "{}";

        try {
            // ✅ LOGIC CHUẨN VỚI FILE YML CỦA BẠN:
            // File YML có sẵn "?key=" -> Code này sẽ tự ghép apiKey vào sau cùng
            String finalUrl = apiUrl.contains("?key=") ? apiUrl + apiKey : apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            GeminiRequest requestBody = new GeminiRequest();
            requestBody.setContents(new ArrayList<>());
            Content content = new Content();
            Part part = new Part();
            part.setText(question);
            content.setParts(Collections.singletonList(part));
            requestBody.getContents().add(content);

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(finalUrl, entity, GeminiResponse.class);
            
            String answer = extractAnswer(response.getBody());
            
            // Lưu log chat (trừ prompt JSON)
            if (!question.contains("Trả về format JSON")) {
                saveChatHistory(question, answer);
            }
            
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "LỖI: " + e.getMessage();
        }
    }

    // ============================================================
    // 3. HELPER METHODS (Giữ nguyên)
    // ============================================================

    private String cleanJsonResult(String result) {
        if (result == null) return "{}";
        String cleaned = result.trim();
        if (cleaned.startsWith("```")) {
            int firstLineBreak = cleaned.indexOf("\n");
            if (firstLineBreak > 0) {
                cleaned = cleaned.substring(firstLineBreak + 1);
            } else {
                cleaned = cleaned.replace("```json", "").replace("```", "");
            }
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private String extractAnswer(GeminiResponse response) {
        try {
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                Candidate candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                    return candidate.getContent().getParts().get(0).getText();
                }
            }
        } catch (Exception e) {}
        return "{}";
    }

    private void saveChatHistory(String question, String answer) {
        try {
            if (question.contains("Trả về format JSON")) return; 
            ChatHistory history = new ChatHistory();
            history.setQuestion(question.length() > 255 ? question.substring(0, 250) + "..." : question);
            if (answer.length() > 4000) history.setAnswer(answer.substring(0, 4000) + "...");
            else history.setAnswer(answer);
            history.setTimestamp(LocalDateTime.now());
            chatHistoryRepository.save(history);
        } catch (Exception e) {
            System.err.println("Lỗi lưu Chat History: " + e.getMessage());
        }
    }

    // DTO CLASSES
    public static class GeminiRequest { private List<Content> contents; public List<Content> getContents() { return contents; } public void setContents(List<Content> contents) { this.contents = contents; } }
    public static class Content { private List<Part> parts; public List<Part> getParts() { return parts; } public void setParts(List<Part> parts) { this.parts = parts; } }
    public static class Part { private String text; public String getText() { return text; } public void setText(String text) { this.text = text; } }
    public static class GeminiResponse { private List<Candidate> candidates; public List<Candidate> getCandidates() { return candidates; } public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; } }
    public static class Candidate { private Content content; public Content getContent() { return content; } public void setContent(Content content) { this.content = content; } }
}