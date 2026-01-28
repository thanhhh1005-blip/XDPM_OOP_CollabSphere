package com.collab.teamservice.repo;

import com.collab.teamservice.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {
  List<Team> findByClassId(Long classId);

  boolean existsByClassIdAndLeaderId(Long classId, String leaderId);

  List<Team> findByClassIdAndLeaderIdIsNotNull(Long classId);

  boolean existsByClassIdAndLeaderIdAndIdNot(Long classId, String leaderId, String id);

  boolean existsByProjectId(String projectId);

  
  boolean existsByProjectIdAndIdNot(String projectId, String id);

  List<Team> findByClassIdIn(List<Long> classIds);

@Query("SELECT tm.userId FROM TeamMember tm WHERE tm.teamId IN (SELECT t.id FROM Team t WHERE t.classId = :classId)")
List<String> findMemberIdsInClass(@Param("classId") Long classId);


}
