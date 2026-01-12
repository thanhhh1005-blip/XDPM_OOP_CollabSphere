package com.collab.projectservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Subject {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String code; // Ví dụ: IT001
}