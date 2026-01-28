package com.collab.teamservice.api;

import com.collab.shared.dto.ApiResponse;
import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.security.RoleGuard;
import com.collab.teamservice.service.TeamAppService;
import com.collab.teamservice.repo.TeamRepository;
import com.collab.teamservice.api.dto.CreateTeamReq;
import com.collab.teamservice.api.dto.TeamResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/teams", "/api/v1/teams"})
@RequiredArgsConstructor
public class TeamController {

  private final TeamAppService service;
  private final TeamRepository teamRepository;

  @PostMapping
  public TeamResponse create(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @RequestBody CreateTeamReq req
  ) {
    RoleGuard.require(role, "LECTURER");

    System.out.println(">>> DEBUG DATA NHẬN ĐƯỢC: " + req.toString());
    System.out.println(">>> DEBUG PROJECT ID: " + req.getProjectId());
    return service.create(
            req.getName(), 
            req.getClassId(), 
            req.getProjectId(), // Lúc này ProjectId sẽ không bị null nữa
            req.getLeaderId(), 
            req.getMemberIds()
        );
  }

  @GetMapping
  public List<TeamResponse> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}/members")
  public List<com.collab.teamservice.api.dto.TeamMemberView> getMembers(@PathVariable("id") String id) {
    return service.getMembers(id);
  }

  @GetMapping("/{id}")
  public TeamResponse getById(@PathVariable("id") String id) {
    return service.getById(id);
  }

  @DeleteMapping("/{id}")
  public void delete(@RequestHeader(value = "X-ROLE", required = false) String role,
                     @PathVariable("id") String id) {
    RoleGuard.require(role, "LECTURER");
    service.delete(id);
  }


  @PutMapping("/{id}")
  public TeamResponse update(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id,
      @RequestParam("name") String name,
      @RequestParam(value = "leaderId", required = false) String leaderId,
      @RequestParam(value = "memberIds", required = false) List<String> memberIds
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.update(id, name, leaderId, memberIds);
  }

  @GetMapping("/student/{username}")
  public ApiResponse<List<TeamResponse>> getMyTeams(@PathVariable("username") String username) {
      return new ApiResponse<>(1000, "Danh sách team sinh viên", service.getTeamsByStudent(username));
  }

  @GetMapping("/lecturer/{username}")
  public ApiResponse<List<TeamResponse>> getTeamsByLecturer(@PathVariable("username") String username) {
      return new ApiResponse<>(1000, "Danh sách team giảng viên", service.getTeamsByLecturer(username));
  }

  @GetMapping("/class/{classId}")
  public ApiResponse<List<Team>> getTeamsByClass(@PathVariable("classId") Long classId) {
      return new ApiResponse<>(1000, "Danh sách Team của lớp", teamRepository.findByClassId(classId));
  }
  @GetMapping("/{id}/name")
  public ResponseEntity<String> getTeamName(@PathVariable String id) {
      // THÊM DÒNG NÀY ĐỂ DEBUG
      System.out.println(">>> TEAM-SERVICE ĐÃ NHẬN REQUEST LẤY TÊN ID: " + id);
      
      return teamRepository.findById(id)
              .map(team -> ResponseEntity.ok(team.getName()))
              .orElse(ResponseEntity.notFound().build());
  }
  
}