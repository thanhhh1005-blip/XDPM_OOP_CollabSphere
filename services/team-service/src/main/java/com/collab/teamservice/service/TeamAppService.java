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
  private final TeamMemberRepository memberRepo;
  private final com.collab.teamservice.client.IdentityServiceClient identityClient;
  private final com.collab.teamservice.client.ClassServiceClient classServiceClient; 
  private final com.collab.teamservice.client.WorkspaceServiceClient workspaceServiceClient;

  @Transactional
  public Team create(String name, Long classId, String projectId, String leaderId, List<String> memberIds) {

    // ✅ normalize input
    String pid = (projectId == null ? null : projectId.trim());
    String lid = (leaderId == null ? null : leaderId.trim());

    // ✅ CHẶN 1 PROJECT CHỈ THUỘC 1 TEAM
    if (pid != null && !pid.isBlank()) {
      if (repo.existsByProjectId(pid)) {
        throw new IllegalArgumentException("Dự án này đã được gán cho team khác.");
      }
    }

    // ✅ CHẶN TRÙNG LEADER TRONG CÙNG LỚP
    if (lid != null && !lid.isBlank()) {
      if (repo.existsByClassIdAndLeaderId(classId, lid)) {
        throw new IllegalArgumentException("Sinh viên này đã là trưởng nhóm của một team trong lớp này.");
      }
    }

    Team t = Team.builder()
        .name(name)
        .classId(classId)
        .projectId(pid)
        .leaderId(lid)
        .status(TeamStatus.ACTIVE)
        .build();

    // ✅ save team trước để có teamId
    t = repo.save(t);
    workspaceServiceClient.createWorkspaceForTeam(t.getId());
    // ✅ gộp members + leader (không trùng)
    Set<String> unique = new HashSet<>();
    if (memberIds != null) unique.addAll(memberIds);
    if (lid != null && !lid.isBlank()) unique.add(lid);

    // ✅ insert team_members
    List<TeamMember> rows = new ArrayList<>();
    for (String uid : unique) {
      if (uid == null || uid.isBlank()) continue;
      String u = uid.trim();

      rows.add(TeamMember.builder()
          .teamId(t.getId())
          .userId(u)
          .memberRole((lid != null && u.equals(lid)) ? MemberRole.LEADER : MemberRole.MEMBER)
          .build());
    }

    if (!rows.isEmpty()) {
      memberRepo.saveAll(rows);
    }

    return t;
  }

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
          m.getContributionPercent().doubleValue()
      );
    }).toList();
  }

  @Transactional(readOnly = true)
  public List<Team> getByClass(Long classId) {
    return repo.findByClassId(classId);
  }

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

  /**
   * NOTE:
   * Controller hiện tại của bạn chưa nhận projectId khi update,
   * nên update() dưới đây giữ nguyên logic cũ (chỉ update name/leader/members).
   * Nếu muốn update projectId + chặn trùng, báo mình, mình gửi luôn TeamController + update mới.
   */
 @Transactional
public Team update(String teamId, String name, String leaderId, List<String> memberIds) {

    // 1. Tìm Team
    Team team = repo.findById(teamId)
        .orElseThrow(() -> new RuntimeException("Team not found"));

    String newLeader = (leaderId == null ? null : leaderId.trim());
    
    // 2. Kiểm tra logic Leader trùng lớp khác (giữ nguyên của em)
    if (newLeader != null && !newLeader.isBlank()) {
        if (repo.existsByClassIdAndLeaderIdAndIdNot(team.getClassId(), newLeader, teamId)) {
            throw new IllegalArgumentException("Sinh viên này đã là trưởng nhóm của một team khác trong lớp này.");
        }
    }

    // 3. Cập nhật thông tin Team
    team.setName(name);
    team.setLeaderId(newLeader);
    repo.save(team);

    // =========================================================
    // 4. LOGIC CẬP NHẬT THÀNH VIÊN: CHỈ THÊM NGƯỜI CHƯA CÓ
    // =========================================================

    // Bước 4.1: Chuẩn bị danh sách User ID mới (gộp Leader + Members)
    Set<String> newUserIds = new HashSet<>();
    if (newLeader != null) newUserIds.add(newLeader);
    if (memberIds != null) {
        for (String id : memberIds) {
            if (id != null && !id.isBlank()) newUserIds.add(id.trim());
        }
    }

    // Bước 4.2: Xóa những người CŨ mà không có trong danh sách MỚI
    List<TeamMember> currentMembers = memberRepo.findByTeamId(teamId);
    for (TeamMember oldMember : currentMembers) {
        if (!newUserIds.contains(oldMember.getUserId())) {
            memberRepo.delete(oldMember);
        }
    }

    // Bước 4.3: Thực hiện gán quyền và Thêm người mới (NẾU CHƯA CÓ)
    for (String userId : newUserIds) {
        // Kiểm tra xem ông này đã có trong DB của team này chưa
        Optional<TeamMember> existing = memberRepo.findByTeamIdAndUserId(teamId, userId);
        
        MemberRole role = userId.equals(newLeader) ? MemberRole.LEADER : MemberRole.MEMBER;

        if (existing.isPresent()) {
            // NẾU ĐÃ CÓ: Chỉ cập nhật lại Role nếu Role thay đổi (Ví dụ từ Member lên Leader)
            TeamMember m = existing.get();
            if (m.getMemberRole() != role) {
                m.setMemberRole(role);
                memberRepo.save(m);
            }
        } else {
            // NẾU CHƯA CÓ: Lúc này mới INSERT (Đảm bảo không bao giờ Duplicate)
            memberRepo.save(
                TeamMember.builder()
                    .teamId(teamId)
                    .userId(userId)
                    .memberRole(role)
                    .joinedAt(java.time.Instant.now()) // Sửa kiểu Instant như lúc nãy
                    .contributionPercent(0.0) // Sửa kiểu Integer như lúc nãy
                    .build()
            );
        }
    }

    return team;
}

  // --- LẤY DANH SÁCH TEAM DÀNH CHO GIẢNG VIÊN ---
@Transactional(readOnly = true)
public List<Team> getTeamsByLecturer(String teacherId) {
    // 1. Gọi sang class-service để lấy danh sách ID các lớp mà GV này dạy
    // Thầy giả sử bạn em đã có Client này
    List<Long> myClassIds = classServiceClient.getClassIdsByTeacher(teacherId);

    if (myClassIds == null || myClassIds.isEmpty()) {
        return List.of();
    }

    // 2. Tìm tất cả các Team thuộc danh sách classId ở trên
    return repo.findByClassIdIn(myClassIds);
}
  public List<Team> getMyTeams(String userId) {
    // 1. Tìm xem mình nằm trong những hàng đợi nào
    List<TeamMember> memberships = memberRepo.findByUserId(userId);
    
    // 2. Lấy danh sách ID team
    List<String> teamIds = memberships.stream()
            .map(TeamMember::getTeamId)
            .toList();

    // 3. Trả về thông tin các Team đó
    return repo.findAllById(teamIds);
  }
  
  // --- LẤY DANH SÁCH TEAM MÀ SINH VIÊN THAM GIA ---
    @Transactional(readOnly = true)
    public List<Team> getTeamsByStudent(String userId) {
        // 1. Tìm tất cả bản ghi trong bảng team_members có userId này
        List<TeamMember> memberships = memberRepo.findByUserId(userId);
        
        // 2. Lấy danh sách ID team từ các bản ghi đó
        List<String> teamIds = memberships.stream()
                .map(TeamMember::getTeamId)
                .toList();

        // 3. Trả về thông tin chi tiết của các Team này
        return repo.findAllById(teamIds);
    }
}
