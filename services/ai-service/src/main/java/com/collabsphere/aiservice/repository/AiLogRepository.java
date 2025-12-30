package com.collabsphere.aiservice.repository;

import com.collabsphere.aiservice.entity.AiGenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiLogRepository extends JpaRepository<AiGenerationLog, Long> {
    // Có thể thêm hàm tìm kiếm theo ngày sau này nếu muốn
}