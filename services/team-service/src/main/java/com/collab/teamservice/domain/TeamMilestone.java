package com.collab.teamservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "team_milestones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMilestone {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String teamId;

  @Column(nullable = false)
  private String title;

  @Column(length = 2000)
  private String description;

  private Integer orderNo;

  private Instant createdAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
  }
}
