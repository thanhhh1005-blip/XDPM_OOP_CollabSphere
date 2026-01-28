package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "milestones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;      
    
    @Column(length = 1000)
    private String description; 

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Long classId;       
    
    @Column(length = 2000)
    private String criteria; 
    
    private String createdBy; 
    private Integer weekNumber;
}