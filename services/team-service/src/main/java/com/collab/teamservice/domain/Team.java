package com.collab.teamservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "teams")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String classId;

  private String projectId;

  @Column(nullable = false)
  private String name;

  private String leaderId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TeamStatus status;

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
