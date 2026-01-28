package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long milestoneId; 
    private String teamId;  

    @Column(columnDefinition = "TEXT")
    private String submissionUrl;
    
    @Column(columnDefinition = "TEXT")
    private String note;          


    private String status; 
    
    private LocalDateTime submittedAt; 
    
    private Double grade; 

    private Double score;     
    private String feedback;
}