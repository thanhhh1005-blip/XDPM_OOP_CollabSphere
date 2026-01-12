package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sprints")
@Data
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; // Ví dụ: "Sprint 1 - Khởi tạo"
    private LocalDate startDate;
    private LocalDate endDate;
    
    private Long projectId; // ID của dự án (tham chiếu từ Project Service)

    @ManyToOne
    @JoinColumn(name = "milestone_id") // Quan hệ: 1 Milestone có nhiều Sprint
    @JsonIgnore
    private Milestone milestone;
    @Column(nullable = false)
    private Long classId;
    private Long subjectId;
}