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
    private String room;        // Phòng học
    
    private Long subjectId;     // ID môn học
    
    // --- CẬP NHẬT MỚI: THÊM DÒNG NÀY ---
    private String teacherId;   // Mã giảng viên (Ví dụ: "GV001")
    // -----------------------------------
    
    // Thông tin chi tiết môn học (khi cần hiển thị)
    private SubjectDTO subject; 
}