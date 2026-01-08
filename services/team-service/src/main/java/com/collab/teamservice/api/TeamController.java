package com.collab.teamservice.api;

import com.collab.teamservice.api.dto.TeamDtos.*;
import com.collab.teamservice.domain.Team;
import com.collab.teamservice.domain.TeamMember;
import com.collab.teamservice.security.ReqContext;
import com.collab.teamservice.service.TeamAppService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

  private final TeamAppService service;

  @PostMapping
  public Team create(@Valid @RequestBody CreateTeamReq req, HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    if (!(ctx.isLecturer() || ctx.isStudent())) {
      throw new RuntimeException("Bạn không có quyền tạo team");
    }
    // demo: creator = X-USER-ID
    return service.createTeam(req.classId(), req.name(), ctx.getUserId());
  }

  @GetMapping
  public List<Team> listByClass(@RequestParam String classId) {
    return service.getTeamsByClass(classId);
  }

  @GetMapping("/{id}")
  public Team get(@PathVariable String id) {
    return service.getTeam(id);
  }

  @GetMapping("/{id}/members")
  public List<TeamMember> members(@PathVariable String id) {
    return service.getMembers(id);
  }

  @PostMapping("/{id}/members")
  public void addMember(@PathVariable String id, @Valid @RequestBody AddMemberReq req, HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    // demo: leader/lecturer mới add
    Team team = service.getTeam(id);
    boolean isLeader = team.getLeaderId() != null && team.getLeaderId().equals(ctx.getUserId());
    if (!(ctx.isLecturer() || isLeader)) {
      throw new RuntimeException("Chỉ giảng viên hoặc trưởng nhóm được thêm thành viên");
    }
    service.addMember(id, req.userId());
  }

  @DeleteMapping("/{id}/members/{userId}")
  public void removeMember(@PathVariable String id, @PathVariable String userId, HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    Team team = service.getTeam(id);
    boolean isLeader = team.getLeaderId() != null && team.getLeaderId().equals(ctx.getUserId());
    if (!(ctx.isLecturer() || isLeader)) {
      throw new RuntimeException("Chỉ giảng viên hoặc trưởng nhóm được xóa thành viên");
    }
    service.removeMember(id, userId);
  }

  @PostMapping("/{id}/leader/{userId}")
  public Team setLeader(@PathVariable String id, @PathVariable String userId, HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    Team team = service.getTeam(id);
    boolean isLeader = team.getLeaderId() != null && team.getLeaderId().equals(ctx.getUserId());
    if (!(ctx.isLecturer() || isLeader)) {
      throw new RuntimeException("Chỉ giảng viên hoặc trưởng nhóm được đổi trưởng nhóm");
    }
    return service.setLeader(id, userId);
  }
}
