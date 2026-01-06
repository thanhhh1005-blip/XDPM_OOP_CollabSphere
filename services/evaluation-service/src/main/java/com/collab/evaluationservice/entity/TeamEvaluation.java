package com.collab.evaluationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_evaluations")
@Data
public class TeamEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teamId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
