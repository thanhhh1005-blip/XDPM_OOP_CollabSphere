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

    private String title; 
    
    private String assignedTo; 
    
    private boolean isCompleted;
    
    private Long milestoneId;
    
    private Long classId; 
}