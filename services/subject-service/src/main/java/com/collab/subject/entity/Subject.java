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
    private String code; 

    @Column(nullable = false)
    private String name; 

    private int credits; 

    @Column(columnDefinition = "TEXT") 
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true; 
}