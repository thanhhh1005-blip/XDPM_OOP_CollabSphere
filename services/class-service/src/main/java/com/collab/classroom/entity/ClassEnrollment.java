package com.collab.classroom.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "class_enrollments")
@Data
public class ClassEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id")
    private Long classId; // Lớp nào

    @Column(name = "student_id")
    private String studentId; // Sinh viên nào (Lưu mã SV, VD: "SV2024001")
}