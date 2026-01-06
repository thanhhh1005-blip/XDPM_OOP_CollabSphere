package com.collab.classroom.entity;

import com.collab.shared.dto.SubjectDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes") // Tên bảng trong DB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_code", unique = true, nullable = false)
    private String classCode; // Mã lớp (VD: SE104.O21)

    // QUAN TRỌNG: Chỉ lưu ID của Subject, không map quan hệ @ManyToOne trực tiếp
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "teacher_id")
    private String teacherId; // <--- MỚI THÊM: Để lưu ID giảng viên (Ví dụ: "GV001" hoặc "10")
    
    private String semester; // Học kỳ (VD: HK1_2024)
    
    private String room; // Phòng học

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Field này dùng để hứng dữ liệu từ Subject-Service đổ vào khi cần hiển thị
    // @Transient nghĩa là không lưu xuống database của Class-Service
    @Transient
    private SubjectDTO subjectDetails;
}