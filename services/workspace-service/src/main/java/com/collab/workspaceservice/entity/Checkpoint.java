package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoints")
@Data
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String submissionUrl; // Link nộp bài (Github/Google Drive)
    private String feedback;      // Nhận xét của giảng viên
    private Integer grade;        // Điểm (0-100)
    private String status;        // PENDING, SUBMITTED, GRADED

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;
}