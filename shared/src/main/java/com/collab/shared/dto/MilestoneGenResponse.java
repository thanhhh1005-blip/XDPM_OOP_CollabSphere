package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneGenResponse {
    private String title;
    private String description;
    private int weekNumber; // Tuần thứ mấy
    private String criteria; // Tiêu chí đánh giá
}
