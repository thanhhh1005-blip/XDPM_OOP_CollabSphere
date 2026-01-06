package com.collab.workspaceservice.entity;

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

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;
}