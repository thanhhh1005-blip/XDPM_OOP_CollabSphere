package com.collab.teamservice.service;

import com.collab.teamservice.Entity.MilestoneStatus;
import com.collab.teamservice.Entity.TeamMilestone;
import com.collab.teamservice.api.dto.CreateMilestoneReq;
import com.collab.teamservice.repo.TeamMilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneAppService {

  private final TeamMilestoneRepository repo;

  @Transactional
  public TeamMilestone create(String teamId, CreateMilestoneReq req) {
    TeamMilestone m = TeamMilestone.builder()
        .teamId(teamId)
        .title(req.title())
        .description(req.description())
        .status(MilestoneStatus.TODO)
        .build();
    return repo.save(m);
  }

  @Transactional(readOnly = true)
  public List<TeamMilestone> list(String teamId) {
    return repo.findByTeamId(teamId);
  }

  @Transactional
  public TeamMilestone markDone(String id) {
    TeamMilestone m = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy milestone: " + id));
    m.setStatus(MilestoneStatus.DONE);
    return repo.save(m);
  }
}
