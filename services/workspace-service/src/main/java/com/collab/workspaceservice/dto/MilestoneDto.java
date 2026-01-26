package com.collab.workspaceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; // Hoặc dùng Date tùy project bạn

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDto {
    private String title;       // Tên cột mốc
    private String description; // Mô tả
    private LocalDateTime startDate; // Ngày bắt đầu
    private LocalDateTime endDate;   // Ngày kết thúc (Deadline)
    
    // Bạn có thể thêm các trường khác cho khớp với Entity Milestone của bạn
}