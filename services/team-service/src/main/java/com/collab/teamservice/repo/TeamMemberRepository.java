package com.collab.teamservice.repo;

import com.collab.teamservice.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, String> {
  List<TeamMember> findByTeamId(String teamId);
  Optional<TeamMember> findByTeamIdAndUserId(String teamId, String userId);
  void deleteByTeamIdAndUserId(String teamId, String userId);
}
