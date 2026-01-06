package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;
    private String code;    // Mã môn
    private String name;    // Tên môn
    private int credits;    // Tín chỉ
    private String description; // Mô tả môn học (nên thêm)
    private Boolean isActive;   // Trạng thái hoạt động (nên thêm)
}