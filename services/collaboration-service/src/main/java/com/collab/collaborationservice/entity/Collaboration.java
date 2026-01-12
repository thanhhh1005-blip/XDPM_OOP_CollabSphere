package com.collab.collaborationservice.entity;

import com.collab.collaborationservice.enums.CollaborationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "collaborations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaboration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long teamId;

    // Tên nhóm / workspace
    @Column(nullable = false)
    private String name;

    // Mô tả
    private String description;

    // Người tạo (userId)
    @Column(nullable = false)
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = CollaborationStatus.ACTIVE;
    }

    // ===== RELATION =====
    @OneToMany(mappedBy = "collaboration", cascade = CascadeType.ALL)
    private List<CollaborationMember> members;
}
