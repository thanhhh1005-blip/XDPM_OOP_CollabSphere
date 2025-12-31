package com.collab.classroom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;     // Mã lớp (Ví dụ: JAVA_K17_01)

    // Quan trọng: Chỉ lưu ID môn học, không map đối tượng Subject
    @Column(nullable = false)
    private Long subjectId;  
    
    private String semester; // Học kỳ (Ví dụ: SPRING_2025)
    
    private String status;   // Trạng thái: PLANNED, OPEN, COMPLETED
}