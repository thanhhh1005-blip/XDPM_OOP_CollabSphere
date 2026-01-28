package com.collab.workspaceservice.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status; 
    private Integer position;
    private String assigneeId;
    private String submissionUrl; 
    private LocalDateTime submittedAt; 
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    @JsonIgnore
    private Workspace workspace; 
    
    @Column(columnDefinition = "boolean default false")
    private Boolean isSubmissionRequired = false; 
    @Column(name = "class_id")
    private Long classId;
    
    @Column(name = "team_id", length = 36) 
    private String teamId;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

}