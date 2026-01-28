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
    private String classCode;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "teacher_id")
    private String teacherId; 
    private String semester;
    
    private String room; 

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Transient
    private SubjectDTO subjectDetails;
}