package com.collab.teamservice.api;

import com.collab.teamservice.Entity.TeamMilestone;
import com.collab.teamservice.api.dto.CreateMilestoneReq;
import com.collab.teamservice.security.RoleGuard;
import com.collab.teamservice.service.MilestoneAppService;
import com.collab.teamservice.service.TeamAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/milestones", "/api/v1/milestones"})
@RequiredArgsConstructor
public class MilestoneController {

  private final MilestoneAppService milestoneService;
  private final TeamAppService teamService;

  @PostMapping("/team/{teamId}")
  public TeamMilestone create(@RequestHeader(value="X-ROLE", required=false) String role,
                              @PathVariable String teamId,
                              @RequestBody CreateMilestoneReq req) {
    RoleGuard.require(role, "LECTURER");
    return milestoneService.create(teamId, req);
  }

  @GetMapping("/team/{teamId}")
  public List<TeamMilestone> list(@PathVariable String teamId) {
    return milestoneService.list(teamId);
  }

  @PostMapping("/{id}/done")
  public TeamMilestone done(@RequestHeader(value="X-ROLE", required=false) String role,
                            @RequestHeader(value="X-USER-ID", required=false) String userId,
                            @RequestParam String teamId,
                            @PathVariable String id) {
    // Student leader mới được done
    RoleGuard.require(role, "STUDENT");
    if (userId == null || userId.isBlank()) throw new RuntimeException("Thiếu X-USER-ID");
    if (!teamService.isLeader(teamId, userId)) throw new RuntimeException("Chỉ leader mới được mark done");
    return milestoneService.markDone(id);
  }
}
