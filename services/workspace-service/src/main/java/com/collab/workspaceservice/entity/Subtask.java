package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subtasks")
@Data
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Boolean isDone = false;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}