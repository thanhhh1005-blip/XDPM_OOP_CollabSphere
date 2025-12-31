package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomDTO {
    private Long id;
    private String code;        // Mã lớp (Ví dụ: SE104.O21)
    private String semester;    // Học kỳ (Ví dụ: HK1_2025)
    private String room;        // Phòng học (nếu cần)
    
    private Long subjectId;     // ID môn học (để lưu xuống DB)
    
    // Quan trọng: Dùng để chứa thông tin đầy đủ của môn học khi trả về cho Frontend
    private SubjectDTO subject; 
}