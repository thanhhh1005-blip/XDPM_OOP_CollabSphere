package com.collab.teamservice.api.dto; // Hoặc package tương ứng của bạn

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    private String id;
    private String name;
    private Long classId;
    
    private String projectId;
    private String projectName; // 👈 Đây là cái bạn đang thiếu
    
    private String leaderId;
    private String leaderName;  // 👈 Thêm cái này cho tiện hiển thị
    
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}