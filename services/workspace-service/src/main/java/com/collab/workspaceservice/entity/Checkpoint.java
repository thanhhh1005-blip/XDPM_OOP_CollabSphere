package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long milestoneId; // Thuộc về cột mốc nào
    private String teamId;    // Nhóm nào nộp

    @Column(columnDefinition = "TEXT")
    private String submissionUrl; // Link nộp bài (Github, Docs...)
    
    @Column(columnDefinition = "TEXT")
    private String note;          // Ghi chú của nhóm trưởng

    // STATUS: PENDING, SUBMITTED, COMPLETED, LATE
    private String status; 
    
    private LocalDateTime submittedAt; // Ngày nộp thực tế
    
    private Double grade; // Điểm số giảng viên chấm (nếu có)
}