package com.collab.shared.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckpointDTO {
    private Long id;
    private Long milestoneId;
    
    private String teamId;    // Giữ cái này để code logic
    private String teamName;  // 🔥 CÁI MỚI CẦN THÊM (Để hiện tên)
    
    private String status;
    private String submissionUrl;
    private Double score;
    private String feedback;
    private String note;
    private LocalDateTime submittedAt;
}