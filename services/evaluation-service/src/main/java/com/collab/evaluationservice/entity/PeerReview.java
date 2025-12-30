package com.collab.evaluationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "peer_reviews")
@Data
public class PeerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromStudentId;
    private Long toStudentId;

    private Integer score;
    private String comment;

    private LocalDateTime createdAt;
}
