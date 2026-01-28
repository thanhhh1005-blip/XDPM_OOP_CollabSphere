package com.collabsphere.aiservice.repository;

import com.collabsphere.aiservice.entity.AiGenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiGenerationLogRepository extends JpaRepository<AiGenerationLog, Long> {
}