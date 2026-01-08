package com.collab.teamservice.repo;

import com.collab.teamservice.domain.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckpointRepository extends JpaRepository<Checkpoint, String> {
  List<Checkpoint> findByTeamId(String teamId);
}
