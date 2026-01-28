package com.collabsphere.aiservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_logs")
public class AiGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(name = "request_content", columnDefinition = "TEXT")
    private String question; 


    @Column(name = "json_response", columnDefinition = "LONGTEXT") 
    private String answer;   

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