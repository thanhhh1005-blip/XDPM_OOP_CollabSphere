package com.collab.teamservice.repo;

import com.collab.teamservice.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {
  List<Team> findByClassId(Long classId);

  // ✅ check leader đã được dùng trong cùng lớp chưa
  boolean existsByClassIdAndLeaderId(Long classId, String leaderId);

  // ✅ lấy danh sách leaderId đã dùng trong lớp (để meta trả về leaderUsed)
  List<Team> findByClassIdAndLeaderIdIsNotNull(Long classId);

  boolean existsByClassIdAndLeaderIdAndIdNot(Long classId, String leaderId, String id);

  // ✅ ADD: chặn project bị gán cho nhiều team
  boolean existsByProjectId(String projectId);

  // ✅ ADD: dùng khi update, loại trừ chính team hiện tại
  boolean existsByProjectIdAndIdNot(String projectId, String id);
}
