package com.collabsphere.aiservice.service;

import com.collabsphere.aiservice.dto.ProjectPlanResponse;
import com.collabsphere.aiservice.entity.ChatHistory;
import com.collabsphere.aiservice.repository.ChatHistoryRepository;
import com.fasterxml.jackson.databind.DeserializationFeature; // 👈 Import quan trọng
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
        
        // 🔥 CẤU HÌNH QUAN TRỌNG: Bỏ qua lỗi nếu AI trả về trường thừa
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ============================================================
    // 1. TẠO KẾ HOẠCH DỰ ÁN
    // ============================================================
    public ProjectPlanResponse generateProjectPlan(String userInput) {
        // Prompt đã được tối ưu để trả về JSON chuẩn
        String prompt = """
            Bạn là một Project Manager chuyên nghiệp. Nhiệm vụ: Lập kế hoạch dự án chi tiết.
            
            YÊU CẦU BẮT BUỘC:
            1. Trả về format JSON thuần túy, KHÔNG dùng Markdown (```json).
            2. Cấu trúc JSON phải chính xác như sau:
            {
              "project_name": "Tên dự án",
              "overview": "Mô tả tổng quan",
              "milestones": [
                {
                  "phase_number": 1,
                  "phase_name": "Tên giai đoạn",
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

        // Làm sạch kết quả (Gọt bỏ markdown thừa)
        String cleanJson = cleanJsonResult(rawResult);
        System.out.println(">>> Clean JSON: " + cleanJson); // Log ra để debug nếu cần

        try {
            return objectMapper.readValue(cleanJson, ProjectPlanResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về đối tượng rỗng có thông báo lỗi thay vì crash app
            ProjectPlanResponse errorResponse = new ProjectPlanResponse();
            errorResponse.setProjectName("Lỗi xử lý dữ liệu AI");
            errorResponse.setOverview("Không thể đọc định dạng trả về: " + e.getMessage());
            return errorResponse;
        }
    }

    // ============================================================
    // 2. GỌI API GEMINI
    // ============================================================
    public String callGemini(String question) {
        if (question == null || question.trim().isEmpty()) return "{}";

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

        try {
            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(finalUrl, entity, GeminiResponse.class);
            String answer = extractAnswer(response.getBody());
            
            // Lưu log chat (nhưng cẩn thận độ dài)
            saveChatHistory(question, answer);
            
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; 
        }
    }

    // ============================================================
    // 3. HELPER METHODS
    // ============================================================

    private String cleanJsonResult(String result) {
        if (result == null) return "{}";
        String cleaned = result.trim();
        // Xử lý cả ```json và ```JSON (viết hoa)
        if (cleaned.startsWith("```")) {
            int firstLineBreak = cleaned.indexOf("\n");
            if (firstLineBreak > 0) {
                cleaned = cleaned.substring(firstLineBreak + 1);
            } else {
                // Trường hợp ```json dính liền không xuống dòng
                if (cleaned.toLowerCase().startsWith("```json")) {
                    cleaned = cleaned.substring(7);
                } else {
                    cleaned = cleaned.substring(3);
                }
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
        } catch (Exception e) {
            // ignore
        }
        return "{}";
    }

    private void saveChatHistory(String question, String answer) {
        try {
            // Không lưu nếu là request tạo JSON plan (để đỡ rác DB)
            // Hoặc chỉ lưu nếu bạn muốn debug
            if (question.contains("Trả về format JSON thuần túy")) return; 

            ChatHistory history = new ChatHistory();
            history.setQuestion(question.length() > 255 ? question.substring(0, 250) + "..." : question);
            
            // Cắt bớt câu trả lời nếu quá dài (cho cột TEXT/VARCHAR)
            if (answer.length() > 4000) { 
                history.setAnswer(answer.substring(0, 4000) + "...");
            } else {
                history.setAnswer(answer);
            }
            history.setTimestamp(LocalDateTime.now());
            chatHistoryRepository.save(history);
        } catch (Exception e) {
            System.err.println("Lỗi lưu Chat History: " + e.getMessage());
        }
    }

    // ============================================================
    // 4. DTO CLASSES
    // ============================================================
    public static class GeminiRequest {
        private List<Content> contents;
        public List<Content> getContents() { return contents; }
        public void setContents(List<Content> contents) { this.contents = contents; }
    }
    public static class Content {
        private List<Part> parts;
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }
    public static class Part {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    public static class GeminiResponse {
        private List<Candidate> candidates;
        public List<Candidate> getCandidates() { return candidates; }
        public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }
    }
    public static class Candidate {
        private Content content;
        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }
    }
}