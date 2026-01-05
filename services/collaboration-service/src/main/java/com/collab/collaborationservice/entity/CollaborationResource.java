package com.collab.collaborationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID từ resource-service
    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private String addedBy;

    private LocalDateTime addedAt;

    @ManyToOne
    @JoinColumn(name = "collaboration_id", nullable = false)
    private Collaboration collaboration;

    @PrePersist
    void onCreate() {
        this.addedAt = LocalDateTime.now();
    }
}
