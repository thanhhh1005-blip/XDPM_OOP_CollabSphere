package com.collab.teamservice.api;

import com.collab.teamservice.Entity.Team;
import com.collab.teamservice.security.RoleGuard;
import com.collab.teamservice.service.TeamAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/teams", "/api/v1/teams"})
@RequiredArgsConstructor
public class TeamController {

  private final TeamAppService service;

  @PostMapping
  public Team create(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @RequestParam String name,
      @RequestParam Long classId,
      @RequestParam(required = false) String projectId,
      @RequestParam(required = false) String leaderId
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.create(name, classId, projectId, leaderId);
  }

  @GetMapping
  public List<Team> getAll() {
    return service.getAll();
  }

  @GetMapping("/{id}")
  public Team getById(@PathVariable String id) {
    return service.getById(id);
  }

  @GetMapping("/class/{classId}")
  public List<Team> getByClass(@PathVariable Long classId) {
    return service.getByClass(classId);
  }
}
