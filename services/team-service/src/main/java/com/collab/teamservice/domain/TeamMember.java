package com.collab.teamservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
  name = "team_members",
  uniqueConstraints = @UniqueConstraint(columnNames = {"teamId", "userId"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMember {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String teamId;

  @Column(nullable = false)
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TeamRole roleInTeam;

  private Instant joinedAt;

  @PrePersist
  void prePersist() {
    joinedAt = Instant.now();
    if (roleInTeam == null) roleInTeam = TeamRole.MEMBER;
  }
}
