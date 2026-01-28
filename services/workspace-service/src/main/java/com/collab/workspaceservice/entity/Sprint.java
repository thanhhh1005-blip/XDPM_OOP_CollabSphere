package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sprints")
@Data
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; 
    private LocalDate startDate;
    private LocalDate endDate;
    
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "milestone_id") 
    @JsonIgnore
    private Milestone milestone;

    
    @Column(name = "class_id") 
    private Long classId; 
  

    @Column(name = "team_id", length = 36)
    private String teamId;
    
    private Long subjectId;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}