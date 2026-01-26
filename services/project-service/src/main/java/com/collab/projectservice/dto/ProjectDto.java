package com.collab.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private String name;
    private String description;
    private String syllabusContent; // <--- Thêm trường này để nhận dữ liệu từ FE
}
