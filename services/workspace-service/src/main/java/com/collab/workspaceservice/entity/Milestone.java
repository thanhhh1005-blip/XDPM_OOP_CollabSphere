package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "milestones")
@Data
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate dueDate;
    private String status; // Ví dụ: OPEN, CLOSED

    // Quan hệ: 1 Milestone có nhiều Task
    @OneToMany(mappedBy = "milestone")
    @JsonIgnore
    private List<Task> tasks;
    
    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Checkpoint> checkpoints;
    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sprint> sprints;

}