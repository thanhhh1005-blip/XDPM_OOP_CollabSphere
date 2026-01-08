package com.collab.teamservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "checkpoint_submissions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CheckpointSubmission {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String checkpointId;

  @Column(nullable = false)
  private String submitterId;

  @Lob
  private String content;

  private Instant submittedAt;

  @PrePersist
  void prePersist() {
    submittedAt = Instant.now();
  }
}
