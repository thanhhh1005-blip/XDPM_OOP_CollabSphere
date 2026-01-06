package com.collab.evaluationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_evaluations")
@Data
public class MemberEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long evaluatorId;

    private Integer score;
    private String comment;

    private LocalDateTime createdAt;
}
