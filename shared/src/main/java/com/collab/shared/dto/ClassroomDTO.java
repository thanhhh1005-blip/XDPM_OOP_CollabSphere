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
    private String teacherId;   // Username giảng viên (Ví dụ: "giangvien")
    
    // --- THÔNG TIN CHI TIẾT (Để hiển thị ra Frontend) ---
    private SubjectDTO subject; // Tên môn, số tín chỉ...
    
    // 👇 THÊM DÒNG NÀY VÀO NHÉ 👇
    private UserDTO teacher;    // Tên thầy cô, avatar, email...
}