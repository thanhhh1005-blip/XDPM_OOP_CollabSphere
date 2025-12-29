package com.collabsphere.aiservice.service;

import com.collabsphere.aiservice.entity.ChatHistory;
import com.collabsphere.aiservice.repository.ChatHistoryRepository;
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

    @Autowired
    public GeminiService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.restTemplate = new RestTemplate();
    }

    public String callGemini(String question) {
        if (question == null || question.trim().isEmpty()) return "Vui lòng nhập câu hỏi.";

        // 1. Xử lý URL
        String finalUrl = apiUrl.contains("?key=") ? apiUrl + apiKey : apiUrl + "?key=" + apiKey;

        // 2. Tạo Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Tạo Body bằng OBJECT (Thay vì Map/String) -> Giúp JSON chuẩn 100%
        GeminiRequest requestBody = new GeminiRequest();
        requestBody.setContents(new ArrayList<>());

        Content content = new Content();
        Part part = new Part();
        part.setText(question); // Gán câu hỏi vào đây
        
        content.setParts(Collections.singletonList(part));
        requestBody.getContents().add(content);

        // 4. Gửi Request
        // Spring Boot sẽ tự động biến Object `requestBody` thành JSON chuẩn
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Nhận kết quả về dưới dạng Object (GeminiResponse) cho dễ xử lý
            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(finalUrl, entity, GeminiResponse.class);
            
            // 5. Lấy câu trả lời
            String answer = extractAnswer(response.getBody());
            saveChatHistory(question, answer);
            
            return answer;

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi gọi AI: " + e.getMessage();
        }
    }

    private String extractAnswer(GeminiResponse response) {
        try {
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                Candidate candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                    return candidate.getContent().getParts().get(0).getText();
                }
            }
            return "AI không phản hồi.";
        } catch (Exception e) {
            return "Lỗi đọc câu trả lời.";
        }
    }

    private void saveChatHistory(String question, String answer) {
        try {
            ChatHistory history = new ChatHistory();
            history.setQuestion(question);
            history.setAnswer(answer);
            history.setTimestamp(LocalDateTime.now());
            chatHistoryRepository.save(history);
        } catch (Exception e) {
            System.err.println("Lỗi lưu DB: " + e.getMessage());
        }
    }

    // ==========================================
    // CÁC CLASS DTO (Dùng để định nghĩa khuôn JSON)
    // ==========================================

    // Khuôn mẫu Request gửi đi
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

    // Khuôn mẫu Response nhận về
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