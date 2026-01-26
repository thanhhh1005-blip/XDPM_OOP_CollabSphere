package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    // Lấy danh sách cột mốc của 1 lớp, sắp xếp theo ngày kết thúc
    List<Milestone> findByClassIdOrderByEndDateAsc(Long classId);
}