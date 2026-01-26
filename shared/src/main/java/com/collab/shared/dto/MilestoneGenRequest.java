package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneGenRequest {
    private String syllabusContent; // Nội dung đề cương
    private int durationWeeks;      // Tổng thời gian (ví dụ: 15 tuần)
    private Long classId;           // <--- QUAN TRỌNG: Để biết tạo cho lớp nào
    private Long projectId;         // Hoặc tạo cho dự án gốc
}
