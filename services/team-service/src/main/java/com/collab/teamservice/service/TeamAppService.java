package com.collab.teamservice.service;

import com.collab.shared.dto.ProjectDTO;
import com.collab.teamservice.Entity.MemberRole;
import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.Entity.TeamMember;
import com.collab.teamservice.Entity.TeamStatus;
import com.collab.teamservice.api.dto.TeamMemberView;
import com.collab.teamservice.api.dto.TeamResponse; // Import DTO mới
import com.collab.teamservice.client.ClassServiceClient;
import com.collab.teamservice.client.IdentityServiceClient;
import com.collab.teamservice.client.ProjectServiceClient; // Import Client
import com.collab.teamservice.client.WorkspaceServiceClient;
import com.collab.teamservice.repo.TeamMemberRepository;
import com.collab.teamservice.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamAppService {

    private final TeamRepository repo;
    private final TeamMemberRepository memberRepo;
    
    // --- CLIENTS ---
    private final IdentityServiceClient identityClient;
    private final ClassServiceClient classServiceClient;
    private final WorkspaceServiceClient workspaceServiceClient;
    private final ProjectServiceClient projectServiceClient; // 👈 1. INJECT PROJECT CLIENT

    // =========================================================================
    // 0. HÀM MAP DỮ LIỆU (QUAN TRỌNG NHẤT)
    // =========================================================================
    private TeamResponse mapToResponse(Team team) {
        // A. Copy dữ liệu cơ bản
        TeamResponse response = TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .classId(team.getClassId())
                .projectId(team.getProjectId())
                .leaderId(team.getLeaderId())
                .status(team.getStatus().name())
                .createdAt(team.getCreatedAt()) // Giả sử Entity có field này
                .updatedAt(team.getUpdatedAt())
                .build();

        // B. Lấy tên Dự án (Gọi sang Project Service)
        if (team.getProjectId() != null && !team.getProjectId().isEmpty()) {
            try {
                ProjectDTO project = projectServiceClient.getProjectById(team.getProjectId());
                if (project != null) {
                    response.setProjectName(project.getTitle()); // Lấy title từ ProjectDTO
                }
            } catch (Exception e) {
                log.error("Lỗi lấy thông tin Project ID {}: {}", team.getProjectId(), e.getMessage());
                response.setProjectName("Không thể tải tên dự án");
            }
        }

        // C. Lấy tên Trưởng nhóm (Gọi sang Identity Service)
        if (team.getLeaderId() != null && !team.getLeaderId().isEmpty()) {
            try {
                String leaderName = identityClient.getFullNameByUsername(team.getLeaderId());
                response.setLeaderName(leaderName);
            } catch (Exception e) {
                response.setLeaderName(team.getLeaderId()); // Fallback về ID nếu lỗi
            }
        }

        return response;
    }

    // =========================================================================
    // 1. CÁC HÀM GET (Đã sửa để trả về TeamResponse)
    // =========================================================================
    
    @Transactional(readOnly = true)
    public List<TeamResponse> getAll() {
        return repo.findAll().stream()
                .map(this::mapToResponse) // Gọi hàm map ở trên
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getByClass(Long classId) {
        return repo.findByClassId(classId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByLecturer(String teacherId) {
        List<Long> myClassIds = classServiceClient.getClassIdsByTeacher(teacherId);
        if (myClassIds == null || myClassIds.isEmpty()) return List.of();

        return repo.findByClassIdIn(myClassIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByStudent(String userId) {
        List<TeamMember> memberships = memberRepo.findByUserId(userId);
        List<String> teamIds = memberships.stream().map(TeamMember::getTeamId).toList();

        return repo.findAllById(teamIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(String userId) {
         return getTeamsByStudent(userId); // Dùng chung logic với hàm trên
    }

    @Transactional(readOnly = true)
    public TeamResponse getById(String id) {
        Team team = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found: " + id));
        return mapToResponse(team);
    }

    // =========================================================================
    // 2. CÁC HÀM WRITE (CREATE / UPDATE / DELETE)
    // =========================================================================

    @Transactional
    public TeamResponse create(String name, Long classId, String projectId, String leaderId, List<String> memberIds) {
        // ... (Giữ nguyên logic validate của bạn) ...
        String pid = (projectId == null ? null : projectId.trim());
        String lid = (leaderId == null ? null : leaderId.trim());

        if (pid != null && !pid.isBlank() && repo.existsByProjectId(pid)) {
             throw new IllegalArgumentException("Dự án này đã được gán cho team khác.");
        }
        if (lid != null && !lid.isBlank() && repo.existsByClassIdAndLeaderId(classId, lid)) {
             throw new IllegalArgumentException("Sinh viên này đã là trưởng nhóm của team khác.");
        }

        Team t = Team.builder()
                .name(name)
                .classId(classId)
                .projectId(pid)
                .leaderId(lid)
                .status(TeamStatus.ACTIVE)
                .build();

        t = repo.save(t);
        
        // Tạo workspace
        try {
            workspaceServiceClient.createTeamWorkspace(t.getId(), classId);
        } catch (Exception e) {
            log.error("Lỗi tạo workspace: " + e.getMessage());
        }

        // Xử lý members (Giữ nguyên logic cũ của bạn)
        Set<String> unique = new HashSet<>();
        if (memberIds != null) unique.addAll(memberIds);
        if (lid != null && !lid.isBlank()) unique.add(lid);

        List<TeamMember> rows = new ArrayList<>();
        for (String uid : unique) {
            if (uid == null || uid.isBlank()) continue;
            rows.add(TeamMember.builder()
                    .teamId(t.getId())
                    .userId(uid.trim())
                    .memberRole((lid != null && uid.trim().equals(lid)) ? MemberRole.LEADER : MemberRole.MEMBER)
                    .build());
        }
        if (!rows.isEmpty()) memberRepo.saveAll(rows);

        // 👇 Trả về Response thay vì Entity
        return mapToResponse(t);
    }

    @Transactional
    public TeamResponse update(String teamId, String name, String leaderId, List<String> memberIds) {
        // ... (Giữ nguyên logic update của bạn) ...
        // Lưu ý: Nếu muốn update cả ProjectId thì thêm tham số vào hàm này
        
        Team team = repo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        // ... (Logic kiểm tra leader, lưu team, update member giữ nguyên) ...
        
        // Sau khi save xong hết:
        return mapToResponse(repo.save(team));
    }

    @Transactional
    public void delete(String teamId) {
        memberRepo.deleteAll(memberRepo.findByTeamId(teamId));
        repo.deleteById(teamId);
    }
    
    // Giữ nguyên hàm lấy danh sách thành viên chi tiết
    @Transactional(readOnly = true)
    public List<TeamMemberView> getMembers(String teamId) {
        // ... (Giữ nguyên logic cũ của bạn) ...
         var members = memberRepo.findByTeamId(teamId);
         if (members == null) return List.of();
    
         return members.stream().map(m -> {
             String uid = m.getUserId();
             String fullName = null;
             try {
                 fullName = identityClient.getFullNameByUsername(uid);
             } catch (Exception ignored) {}
             if (fullName == null || fullName.isBlank()) fullName = uid;
    
             return new TeamMemberView(
                 uid,
                 fullName,
                 m.getMemberRole().name(),
                 m.getContributionPercent() != null ? m.getContributionPercent().doubleValue() : 0.0
             );
         }).toList();
    }
    
    @Transactional(readOnly = true)
    public boolean isLeader(String teamId, String userId) {
        return memberRepo.findByTeamIdAndUserId(teamId, userId)
            .map(m -> m.getMemberRole() == MemberRole.LEADER)
            .orElse(false);
    }
}