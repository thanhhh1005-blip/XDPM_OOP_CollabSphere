package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_tasks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Tên công việc (VD: Tạo Database)
    
    private String assignedTo; // Username của người làm (VD: student1)
    
    private boolean isCompleted; // Trạng thái hoàn thành
    
    private Long milestoneId; // Thuộc về cột mốc nào
    
    private Long classId; 
}