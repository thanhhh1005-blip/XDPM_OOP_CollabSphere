package com.collab.teamservice.repo;

import com.collab.teamservice.domain.TeamMilestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMilestoneRepository extends JpaRepository<TeamMilestone, String> {
  List<TeamMilestone> findByTeamIdOrderByOrderNoAsc(String teamId);
}
