package com.collab.evaluationservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PeerReviewResponseDTO {

    private Long id;
    private Long fromStudentId;
    private Long toStudentId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
