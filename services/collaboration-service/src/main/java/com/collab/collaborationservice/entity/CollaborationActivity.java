package com.collab.collaborationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // COLLAB_CREATED, MEMBER_ADDED, RESOURCE_SHARED, ...
    @Column(nullable = false)
    private String action;

    private String description;

    @Column(nullable = false)
    private String performedBy;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "collaboration_id", nullable = false)
    private Collaboration collaboration;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
