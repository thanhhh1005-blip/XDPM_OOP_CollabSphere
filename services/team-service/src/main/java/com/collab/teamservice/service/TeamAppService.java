package com.collab.teamservice.service;

import com.collab.teamservice.Entity.MemberRole;
import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.Entity.TeamStatus;
import com.collab.teamservice.repo.TeamMemberRepository;
import com.collab.teamservice.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamAppService {

  private final TeamRepository repo;
  private final TeamMemberRepository memberRepo; // ✅ ADD

  @Transactional
  public Team create(String name, Long classId, String projectId, String leaderId) {
    Team t = Team.builder()
        .name(name)
        .classId(classId)          // ✅ Long
        .projectId(projectId)
        .leaderId(leaderId)
        .status(TeamStatus.ACTIVE)
        .build();
    return repo.save(t);
  }



  // ✅ NEW: lấy tất cả team (TeamList gọi GET /api/v1/teams)
  @Transactional(readOnly = true)
  public List<Team> getAll() {
    return repo.findAll();
  }

@Transactional(readOnly = true)
public List<Team> getByClass(Long classId) {   // ✅ String -> Long
  return repo.findByClassId(classId);
}


  // ✅ ADD: MilestoneController đang gọi hàm này
  @Transactional(readOnly = true)
  public boolean isLeader(String teamId, String userId) {
    return memberRepo.findByTeamIdAndUserId(teamId, userId)
        .map(m -> m.getMemberRole() == MemberRole.LEADER)
        .orElse(false);
  }

  @Transactional(readOnly = true)
  public Team getById(String id) {
    return repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Team not found: " + id));
  }

}
