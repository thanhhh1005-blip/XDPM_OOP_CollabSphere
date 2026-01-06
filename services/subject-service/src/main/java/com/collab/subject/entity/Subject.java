package com.collab.subject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects") // Tên bảng trong MySQL sẽ là 'subjects'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", unique = true, nullable = false)
    private String code; // Mã môn (VD: SE104)

    @Column(nullable = false)
    private String name; // Tên môn học

    private int credits; // Số tín chỉ

    @Column(columnDefinition = "TEXT") // Để lưu mô tả dài
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true; // Mặc định môn học mới tạo sẽ Active
}