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
    private String status; // TODO, DONE
    private Integer position;
    private Long assigneeId;
    private String submissionUrl; // Link bài nộp của sinh viên
    private LocalDateTime submittedAt; // Thời gian nộp

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    @JsonIgnore
    private Workspace workspace; 
    
    @Column(columnDefinition = "boolean default false")
    private Boolean isSubmissionRequired = false; // Giảng viên tick vào đây thì mới bắt nộp

    @Column(name = "class_id")
    private Long classId;
    
    @Column(name = "team_id", length = 36) 
    private String teamId;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    // THÊM: Task thuộc về 1 Milestone nào đó
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    // THÊM: 1 Task có nhiều Subtask nhỏ bên trong
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Subtask> subtasks;
}