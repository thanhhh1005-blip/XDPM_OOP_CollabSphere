package com.collabsphere.aiservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_logs") // 1. Trỏ vào bảng DB thật
public class AiGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Map cột 'request_content' -> thành biến 'question' (Frontend cần tên này)
    @Column(name = "request_content", columnDefinition = "TEXT")
    private String question; 

    // 3. Map cột 'json_response' -> thành biến 'answer' (Frontend cần tên này)
    @Column(name = "json_response", columnDefinition = "LONGTEXT") 
    private String answer;   

    // 4. Map cột 'created_at' -> thành biến 'timestamp' (Frontend cần tên này)
    @Column(name = "created_at")
    private LocalDateTime timestamp;

    // --- Constructor ---
    public AiGenerationLog() {}

    public AiGenerationLog(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters and Setters (Dùng tên mới để JSON trả về đúng ý Frontend) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}