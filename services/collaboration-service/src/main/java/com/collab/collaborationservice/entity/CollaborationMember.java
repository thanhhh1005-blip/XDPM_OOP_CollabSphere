package com.collab.collaborationservice.entity;

import com.collab.collaborationservice.enums.CollaborationRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "collaboration_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // userId từ auth-service
    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationRole role;

    @ManyToOne
    @JoinColumn(name = "collaboration_id", nullable = false)
    private Collaboration collaboration;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
