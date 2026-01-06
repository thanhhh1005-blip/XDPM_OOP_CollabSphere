package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

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
}