package com.collab.teamservice.api;

import com.collab.shared.dto.ApiResponse;
import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.security.RoleGuard;
import com.collab.teamservice.service.TeamAppService;
import com.collab.teamservice.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/teams", "/api/v1/teams"})
@RequiredArgsConstructor
public class TeamController {

  private final TeamAppService service;
  private final TeamRepository teamRepository;

  @PostMapping
  public Team create(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @RequestParam("name") String name,
      @RequestParam("classId") Long classId,
      @RequestParam(value = "projectId", required = false) String projectId,
      @RequestParam(value = "leaderId", required = false) String leaderId,
      @RequestParam(value = "memberIds", required = false) List<String> memberIds
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.create(name, classId, projectId, leaderId, memberIds);
  }

  @GetMapping
  public List<Team> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}/members")
  public List<com.collab.teamservice.api.dto.TeamMemberView> getMembers(@PathVariable("id") String id) {
    return service.getMembers(id);
  }

  @GetMapping("/{id}")
  public Team getById(@PathVariable("id") String id) {
    return service.getById(id);
  }

  @DeleteMapping("/{id}")
  public void delete(@RequestHeader(value = "X-ROLE", required = false) String role,
                     @PathVariable("id") String id) {
    RoleGuard.require(role, "LECTURER");
    service.delete(id);
  }


  @PutMapping("/{id}")
  public Team update(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id,
      @RequestParam("name") String name,
      @RequestParam(value = "leaderId", required = false) String leaderId,
      @RequestParam(value = "memberIds", required = false) List<String> memberIds
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.update(id, name, leaderId, memberIds);
  }

  // API lấy danh sách team của sinh viên cụ thể
  @GetMapping("/student/{username}")
  public ApiResponse<List<Team>> getMyTeams(@PathVariable("username") String username) {
      // Bọc ApiResponse cho đồng bộ
      return new ApiResponse<>(1000, "Danh sách team sinh viên", service.getTeamsByStudent(username));
  }

  // GET /api/v1/teams/lecturer/{username}
  @GetMapping("/lecturer/{username}")
  public ApiResponse<List<Team>> getTeamsByLecturer(@PathVariable("username") String username) {
      // Bọc ApiResponse cho đồng bộ
      return new ApiResponse<>(1000, "Danh sách team giảng viên", service.getTeamsByLecturer(username));
  }

  // ✅ GIỮ LẠI HÀM MỚI NÀY (GET /api/v1/teams/class/{classId})
  @GetMapping("/class/{classId}")
  public ApiResponse<List<Team>> getTeamsByClass(@PathVariable("classId") Long classId) {
      return new ApiResponse<>(1000, "Danh sách Team của lớp", teamRepository.findByClassId(classId));
  }
}