package com.collab.teamservice.api;

import com.collab.teamservice.api.dto.TeamDtos.CreateMilestoneReq;
import com.collab.teamservice.domain.Team;
import com.collab.teamservice.domain.TeamMilestone;
import com.collab.teamservice.security.ReqContext;
import com.collab.teamservice.service.TeamAppService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams/{teamId}/milestones")
public class MilestoneController {

  private final TeamAppService service;

  @PostMapping
  public TeamMilestone create(@PathVariable String teamId,
                              @Valid @RequestBody CreateMilestoneReq req,
                              HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    Team t = service.getTeam(teamId);
    boolean isLeader = t.getLeaderId() != null && t.getLeaderId().equals(ctx.getUserId());
    if (!(ctx.isLecturer() || isLeader)) {
      throw new RuntimeException("Chỉ giảng viên hoặc trưởng nhóm được tạo milestone");
    }
    return service.addMilestone(teamId, req.title(), req.description(), req.orderNo());
  }

  @GetMapping
  public List<TeamMilestone> list(@PathVariable String teamId) {
    return service.listMilestones(teamId);
  }
}
