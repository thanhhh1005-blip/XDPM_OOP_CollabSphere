package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subtasks") // Tên bảng trong DB
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tên Checkpoint (VD: Thiết kế DB)
    
    private String assignedTo; // Username người làm
    
    private boolean isCompleted; // Trạng thái hoàn thành
    
    private Long milestoneId; // Thuộc về cột mốc nào
    
    private String teamId; // ✅ QUAN TRỌNG: Checkpoint này của Team nào (Vì 1 milestone có nhiều team cùng làm)

    private Double score;
    private String comment;
}