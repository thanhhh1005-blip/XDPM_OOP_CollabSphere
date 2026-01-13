package com.collab.teamservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="team_milestones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMilestone {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable=false)
  private String teamId;

  @Column(nullable=false)
  private String title;

  @Lob
  @Column(columnDefinition="LONGTEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private MilestoneStatus status;

  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
    updatedAt = createdAt;
    if (status == null) status = MilestoneStatus.TODO;
  }

  @PreUpdate
  void preUpdate() { updatedAt = Instant.now(); }
}
