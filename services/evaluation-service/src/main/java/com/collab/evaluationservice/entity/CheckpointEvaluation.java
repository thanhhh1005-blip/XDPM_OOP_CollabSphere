package com.collab.evaluationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoint_evaluations")
@Data
public class CheckpointEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long checkpointId;
    private Long evaluatorId;

    private Integer score;
    private String comment;

    private LocalDateTime createdAt;
}

