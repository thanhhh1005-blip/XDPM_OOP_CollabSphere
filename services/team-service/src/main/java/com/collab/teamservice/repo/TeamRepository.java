package com.collab.teamservice.repo;

import com.collab.teamservice.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {
  List<Team> findByClassId(Long classId);
}
