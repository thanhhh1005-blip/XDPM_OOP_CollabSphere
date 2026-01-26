package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "milestones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // Ví dụ: Giai đoạn 1 - Khởi tạo
    
    @Column(length = 1000)
    private String description; // Ví dụ: Nộp tài liệu SRS...

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Long classId;       // Cột mốc này thuộc về lớp nào
    
    // Tiêu chí đánh giá (có thể lưu dạng JSON string hoặc text đơn giản)
    @Column(length = 2000)
    private String criteria; 
    
    private String createdBy;   // Người tạo (Giảng viên)
    private Integer weekNumber;
}