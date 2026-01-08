package com.collab.teamservice.repo;

import com.collab.teamservice.domain.CheckpointSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckpointSubmissionRepository extends JpaRepository<CheckpointSubmission, String> {
  List<CheckpointSubmission> findByCheckpointIdOrderBySubmittedAtDesc(String checkpointId);
}
