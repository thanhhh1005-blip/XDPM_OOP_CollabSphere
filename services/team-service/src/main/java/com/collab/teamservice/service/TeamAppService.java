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

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamAppService {

    private final TeamRepository repo;
    private final TeamMemberRepository memberRepo;
    private final TeamRepository teamRepository;
    // --- CLIENTS ---
    private final IdentityServiceClient identityClient;
    private final ClassServiceClient classServiceClient;
    private final WorkspaceServiceClient workspaceServiceClient;
    private final ProjectServiceClient projectServiceClient; 
    private TeamResponse mapToResponse(Team team) {

    TeamResponse response = TeamResponse.builder()
            .id(team.getId())
            .name(team.getName())
            .classId(team.getClassId())
            .projectId(team.getProjectId())
            .leaderId(team.getLeaderId())
            .status(team.getStatus().name())
            .build();
    System.out.println("🛠️ Mapping Team ID: " + team.getId() + " với tên: " + team.getName());
    if (team.getProjectId() != null) {
      
        ProjectDTO project = projectServiceClient.getProjectById(team.getProjectId());
        
        if (project != null) {
            response.setProjectName(project.getTitle());
        } else {
            response.setProjectName("Không xác định (Lỗi Project)");
        }
    }

    if (team.getLeaderId() != null) {
        try {
            String leaderName = identityClient.getFullNameByUsername(team.getLeaderId());
            response.setLeaderName(leaderName);
        } catch (Exception e) {
            response.setLeaderName(team.getLeaderId()); // Fallback về ID
        }
    }

    return response;
}

    
    @Transactional(readOnly = true)
    public List<TeamResponse> getAll() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
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
         return getTeamsByStudent(userId); 
    }

    public TeamResponse getById(String id) {
    System.out.println("🔍 TeamAppService đang tìm ID: [" + id + "]"); 
    String cleanId = id.trim(); 

    Team team = repo.findById(cleanId)
            .orElseThrow(() -> {
                System.err.println("❌ Database báo: KHÔNG TÌM THẤY team với ID: [" + cleanId + "]");
                return new RuntimeException("Team not found: " + cleanId);
            });

    // 4. Nếu tìm thấy
    System.out.println("✅ Database báo: TÌM THẤY team tên là: " + team.getName());
    
    return mapToResponse(team);
}

    @Transactional
    public TeamResponse create(String name, Long classId, String projectId, String leaderId, List<String> memberIds) {
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
        
        try {
            workspaceServiceClient.createTeamWorkspace(t.getId(), classId);
        } catch (Exception e) {
            log.error("Lỗi tạo workspace: " + e.getMessage());
        }

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

        return mapToResponse(t);
    }

    @Transactional
    public TeamResponse update(String teamId, String name, String leaderId, List<String> memberIds) {
        Team team = repo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
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

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getTeamName(@PathVariable String id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok(team.getName()))
                .orElse(ResponseEntity.notFound().build());
    }

    
}