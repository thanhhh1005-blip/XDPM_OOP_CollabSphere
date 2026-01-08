package com.collab.teamservice.api;

import com.collab.teamservice.api.dto.TeamDtos.CreateCheckpointReq;
import com.collab.teamservice.api.dto.TeamDtos.SubmitCheckpointReq;
import com.collab.teamservice.domain.Checkpoint;
import com.collab.teamservice.domain.CheckpointSubmission;
import com.collab.teamservice.domain.Team;
import com.collab.teamservice.security.ReqContext;
import com.collab.teamservice.service.TeamAppService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckpointController {

  private final TeamAppService service;

  @PostMapping("/teams/{teamId}/checkpoints")
  public Checkpoint create(@PathVariable String teamId,
                           @Valid @RequestBody CreateCheckpointReq req,
                           HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);

    Team t = service.getTeam(teamId);
    boolean isLeader = t.getLeaderId() != null && t.getLeaderId().equals(ctx.getUserId());
    if (!(ctx.isLecturer() || isLeader)) {
      throw new RuntimeException("Chỉ giảng viên hoặc trưởng nhóm được tạo checkpoint");
    }

    return service.createCheckpoint(
      teamId,
      req.milestoneId(),
      req.title(),
      req.description(),
      req.assigneeId(),
      ctx.getUserId()
    );
  }

  @GetMapping("/teams/{teamId}/checkpoints")
  public List<Checkpoint> list(@PathVariable String teamId) {
    return service.listCheckpoints(teamId);
  }

  @PostMapping("/checkpoints/{id}/submit")
  public Checkpoint submit(@PathVariable String id,
                           @Valid @RequestBody SubmitCheckpointReq req,
                           HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    return service.submitCheckpoint(id, ctx, req.content());
  }

  @PostMapping("/checkpoints/{id}/done")
  public Checkpoint done(@PathVariable String id, HttpServletRequest http) {
    ReqContext ctx = ReqContext.from(http);
    return service.markCheckpointDone(id, ctx);
  }

  @GetMapping("/checkpoints/{id}/submissions")
  public List<CheckpointSubmission> submissions(@PathVariable String id) {
    return service.getSubmissions(id);
  }
}
