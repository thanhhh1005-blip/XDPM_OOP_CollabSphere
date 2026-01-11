package com.collab.projectservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "milestones")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "project_id")
    private String projectId; // Khóa ngoại tới projects

    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự hiển thị
}