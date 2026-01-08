package com.collab.teamservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "checkpoints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Checkpoint {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String teamId;

  private String milestoneId;

  @Column(nullable = false)
  private String title;

  @Column(length = 2000)
  private String description;

  private String assigneeId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CheckpointStatus status;

  private String createdBy;
  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
    updatedAt = createdAt;
    if (status == null) status = CheckpointStatus.OPEN;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
