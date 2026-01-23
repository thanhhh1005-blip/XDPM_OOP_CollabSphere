package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassMemberDTO {
    private String userId;   // ID của user (teacherId hoặc studentId)
    private String role;     // "TEACHER" hoặc "STUDENT"
    private String fullName; // Tên hiển thị (nếu có)
}