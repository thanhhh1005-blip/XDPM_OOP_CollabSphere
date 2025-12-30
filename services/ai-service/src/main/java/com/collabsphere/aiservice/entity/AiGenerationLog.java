package com.collabsphere.aiservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_logs")
public class AiGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String requestContent; // Nội dung đề cương người dùng nhập

    @Column(columnDefinition = "LONGTEXT") // Dùng LONGTEXT vì kết quả JSON rất dài
    private String jsonResponse;   // Kết quả AI trả về

    private LocalDateTime createdAt;

    // Constructor
    public AiGenerationLog() {}

    public AiGenerationLog(String requestContent, String jsonResponse) {
        this.requestContent = requestContent;
        this.jsonResponse = jsonResponse;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRequestContent() { return requestContent; }
    public void setRequestContent(String requestContent) { this.requestContent = requestContent; }
    public String getJsonResponse() { return jsonResponse; }
    public void setJsonResponse(String jsonResponse) { this.jsonResponse = jsonResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}