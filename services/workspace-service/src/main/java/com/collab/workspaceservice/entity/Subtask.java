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
    private String title; 
    
    private String assignedTo; 
    
    private boolean isCompleted; 
    
    private Long milestoneId; 
    
    private String teamId; 

    private Double score;
    private String comment;
}