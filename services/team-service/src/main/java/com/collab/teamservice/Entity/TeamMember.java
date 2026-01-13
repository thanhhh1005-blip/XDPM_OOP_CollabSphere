package com.collab.teamservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "team_members",
  uniqueConstraints = @UniqueConstraint(name="uk_team_user", columnNames={"team_id","user_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMember {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name="team_id", nullable=false)
  private String teamId;

  @Column(name="user_id", nullable=false)
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column(name="member_role", nullable=false)
  private MemberRole memberRole;

  private Integer contributionPercent;

  private Instant joinedAt;

  @PrePersist
  void prePersist() {
    joinedAt = Instant.now();
    if (contributionPercent == null) contributionPercent = 0;
    if (memberRole == null) memberRole = MemberRole.MEMBER;
  }
}
