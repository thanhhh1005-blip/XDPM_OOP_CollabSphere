package com.collab.teamservice.service;

import com.collab.teamservice.Entity.MemberRole;
import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.Entity.TeamMember;
import com.collab.teamservice.Entity.TeamStatus;
import com.collab.teamservice.repo.TeamMemberRepository;
import com.collab.teamservice.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamAppService {

  private final TeamRepository repo;
  private final TeamMemberRepository memberRepo; // ✅ ADD
  private final com.collab.teamservice.client.IdentityServiceClient identityClient; // ✅ ADD

  @Transactional
  public Team create(String name, Long classId, String projectId, String leaderId, List<String> memberIds) {

    // ✅ CHẶN TRÙNG LEADER TRONG CÙNG LỚP
    if (leaderId != null && !leaderId.trim().isBlank()) {
      if (repo.existsByClassIdAndLeaderId(classId, leaderId.trim())) {
        throw new IllegalArgumentException("Sinh viên này đã là trưởng nhóm của một team trong lớp này.");
      }
    }

    Team t = Team.builder()
        .name(name)
        .classId(classId)          // ✅ Long
        .projectId(projectId)
        .leaderId(leaderId)
        .status(TeamStatus.ACTIVE)
        .build();

    // ✅ save team trước để có teamId
    t = repo.save(t);

    // ✅ gộp members + leader (không trùng)
    Set<String> unique = new HashSet<>();
    if (memberIds != null) unique.addAll(memberIds);
    if (leaderId != null && !leaderId.trim().isBlank()) unique.add(leaderId.trim());

    // ✅ insert team_members
    List<TeamMember> rows = new ArrayList<>();
    for (String uid : unique) {
      if (uid == null || uid.isBlank()) continue;

      rows.add(TeamMember.builder()
          .teamId(t.getId())
          .userId(uid)
          .memberRole((leaderId != null && uid.equals(leaderId.trim())) ? MemberRole.LEADER : MemberRole.MEMBER)
          .build());
    }

    if (!rows.isEmpty()) {
      memberRepo.saveAll(rows);
    }

    return t;
  }

  // ✅ NEW: lấy tất cả team (TeamList gọi GET /api/v1/teams)
  @Transactional(readOnly = true)
  public List<Team> getAll() {
    return repo.findAll();
  }

  @Transactional(readOnly = true)
public List<com.collab.teamservice.api.dto.TeamMemberView> getMembers(String teamId) {
  var members = memberRepo.findByTeamId(teamId);
  if (members == null) return List.of();

  return members.stream().map(m -> {
    String uid = m.getUserId();

    String fullName = null;
    try {
      fullName = identityClient.getFullNameByUsername(uid);
    } catch (Exception ignored) {}
    if (fullName == null || fullName.isBlank()) fullName = uid;

    return new com.collab.teamservice.api.dto.TeamMemberView(
        uid,
        fullName,
        m.getMemberRole().name(),
        m.getContributionPercent()
    );
  }).toList();
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

  @Transactional
public void delete(String teamId) {
  // xóa members trước
  memberRepo.deleteAll(memberRepo.findByTeamId(teamId));
  repo.deleteById(teamId);
}
@Transactional
public Team update(String teamId, String name, String leaderId, List<String> memberIds) {

  Team team = repo.findById(teamId)
      .orElseThrow(() -> new RuntimeException("Team not found"));

  String newLeader = (leaderId == null ? null : leaderId.trim());
  if (newLeader != null && !newLeader.isBlank()) {

    // ✅ CHỈ CHẶN LEADER TRÙNG TRONG CÙNG LỚP (trừ chính team này)
    if (repo.existsByClassIdAndLeaderIdAndIdNot(team.getClassId(), newLeader, teamId)) {
      throw new IllegalArgumentException("Sinh viên này đã là trưởng nhóm của một team khác trong lớp này.");
    }
  }

  // 1) update thông tin team
  team.setName(name);
  team.setLeaderId(newLeader);
  repo.save(team);

  // 2) Xóa member cũ
  memberRepo.deleteByTeamId(teamId);

  // 3) Add leader (nếu có)
  if (newLeader != null && !newLeader.isBlank()) {
    memberRepo.save(
        TeamMember.builder()
            .teamId(teamId)
            .userId(newLeader)
            .memberRole(MemberRole.LEADER)
            .build()
    );
  }

  // 4) Add members (KHÔNG CHECK TRÙNG MEMBER)
  if (memberIds != null) {
    for (String uid : memberIds) {
      if (uid == null) continue;
      String m = uid.trim();
      if (m.isBlank()) continue;
      if (newLeader != null && m.equals(newLeader)) continue;

      memberRepo.save(
          TeamMember.builder()
              .teamId(teamId)
              .userId(m)
              .memberRole(MemberRole.MEMBER)
              .build()
      );
    }
  }

  return team;
}


}



