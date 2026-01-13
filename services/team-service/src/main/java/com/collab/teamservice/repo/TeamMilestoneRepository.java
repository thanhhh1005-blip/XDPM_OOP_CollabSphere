package com.collab.teamservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.collab.teamservice.Entity.TeamMilestone;

import java.util.List;

public interface TeamMilestoneRepository extends JpaRepository<TeamMilestone, String> {
  List<TeamMilestone> findByTeamId(String teamId);
}
