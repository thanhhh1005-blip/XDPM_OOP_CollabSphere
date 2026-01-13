package com.collab.teamservice.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 36, nullable = false)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(name = "class_id", nullable = false)
  private Long classId; // ✅ khớp class-service

  @Column(name = "project_id")
  private String projectId;

  @Column(name = "leader_id")
  private String leaderId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TeamStatus status; // ✅ enum: ACTIVE, DONE...

  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
    updatedAt = createdAt;
    if (status == null) status = TeamStatus.ACTIVE;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
