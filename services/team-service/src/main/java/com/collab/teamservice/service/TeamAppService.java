package com.collab.teamservice.service;

import com.collab.teamservice.domain.*;
import com.collab.teamservice.repo.*;
import com.collab.teamservice.security.ReqContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamAppService {

  private final TeamRepository teamRepo;
  private final TeamMemberRepository memberRepo;
  private final TeamMilestoneRepository milestoneRepo;
  private final CheckpointRepository checkpointRepo;
  private final CheckpointSubmissionRepository submissionRepo;

  // ===== Team =====

  @Transactional
  public Team createTeam(String classId, String name, String creatorUserId) {
    Team team = Team.builder()
      .classId(classId)
      .name(name)
      .leaderId(creatorUserId) // mặc định creator là leader (demo)
      .status(TeamStatus.ACTIVE)
      .build();

    Team saved = teamRepo.save(team);

    // add leader as member
    memberRepo.save(TeamMember.builder()
      .teamId(saved.getId())
      .userId(creatorUserId)
      .roleInTeam(TeamRole.LEADER)
      .build());

    return saved;
  }

  @Transactional(readOnly = true)
  public List<Team> getTeamsByClass(String classId) {
    return teamRepo.findByClassId(classId);
  }

  @Transactional(readOnly = true)
  public Team getTeam(String id) {
    return teamRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy team: " + id));
  }

  @Transactional(readOnly = true)
  public List<TeamMember> getMembers(String teamId) {
    return memberRepo.findByTeamId(teamId);
  }

  @Transactional
  public void addMember(String teamId, String userId) {
    // tránh trùng
    memberRepo.findByTeamIdAndUserId(teamId, userId).ifPresent(m -> {
      throw new RuntimeException("Thành viên đã tồn tại trong team");
    });

    memberRepo.save(TeamMember.builder()
      .teamId(teamId)
      .userId(userId)
      .roleInTeam(TeamRole.MEMBER)
      .build());
  }

  @Transactional
  public void removeMember(String teamId, String userId) {
    memberRepo.deleteByTeamIdAndUserId(teamId, userId);
  }

  @Transactional
  public Team setLeader(String teamId, String newLeaderId) {
    Team t = getTeam(teamId);

    // đảm bảo user là member
    memberRepo.findByTeamIdAndUserId(teamId, newLeaderId)
      .orElseThrow(() -> new RuntimeException("User chưa là thành viên team"));

    // set role trong member
    memberRepo.findByTeamIdAndUserId(teamId, t.getLeaderId()).ifPresent(oldLeader -> {
      oldLeader.setRoleInTeam(TeamRole.MEMBER);
      memberRepo.save(oldLeader);
    });

    TeamMember newLeader = memberRepo.findByTeamIdAndUserId(teamId, newLeaderId).get();
    newLeader.setRoleInTeam(TeamRole.LEADER);
    memberRepo.save(newLeader);

    t.setLeaderId(newLeaderId);
    return teamRepo.save(t);
  }

  // ===== Milestones =====

  @Transactional
  public TeamMilestone addMilestone(String teamId, String title, String description, Integer orderNo) {
    // ensure team exists
    getTeam(teamId);

    return milestoneRepo.save(TeamMilestone.builder()
      .teamId(teamId)
      .title(title)
      .description(description)
      .orderNo(orderNo)
      .build());
  }

  @Transactional(readOnly = true)
  public List<TeamMilestone> listMilestones(String teamId) {
    return milestoneRepo.findByTeamIdOrderByOrderNoAsc(teamId);
  }

  // ===== Checkpoints =====

  @Transactional
  public Checkpoint createCheckpoint(String teamId, String milestoneId, String title, String description,
                                     String assigneeId, String createdBy) {
    // ensure team exists
    getTeam(teamId);

    return checkpointRepo.save(Checkpoint.builder()
      .teamId(teamId)
      .milestoneId(milestoneId)
      .title(title)
      .description(description)
      .assigneeId(assigneeId)
      .createdBy(createdBy)
      .status(CheckpointStatus.OPEN)
      .build());
  }

  @Transactional(readOnly = true)
  public List<Checkpoint> listCheckpoints(String teamId) {
    return checkpointRepo.findByTeamId(teamId);
  }

  @Transactional
  public Checkpoint submitCheckpoint(String checkpointId, ReqContext ctx, String content) {
    Checkpoint cp = checkpointRepo.findById(checkpointId)
      .orElseThrow(() -> new RuntimeException("Không tìm thấy checkpoint: " + checkpointId));

    // basic auth: chỉ member của team mới submit (demo)
    memberRepo.findByTeamIdAndUserId(cp.getTeamId(), ctx.getUserId())
      .orElseThrow(() -> new RuntimeException("Bạn không thuộc team này"));

    // nếu có assignee thì chỉ assignee submit (optional)
    if (cp.getAssigneeId() != null && !cp.getAssigneeId().isBlank()
      && !cp.getAssigneeId().equals(ctx.getUserId())) {
      throw new RuntimeException("Checkpoint này được giao cho người khác");
    }

    submissionRepo.save(CheckpointSubmission.builder()
      .checkpointId(cp.getId())
      .submitterId(ctx.getUserId())
      .content(content)
      .build());

    cp.setStatus(CheckpointStatus.SUBMITTED);
    return checkpointRepo.save(cp);
  }

  @Transactional
  public Checkpoint markCheckpointDone(String checkpointId, ReqContext ctx) {
    Checkpoint cp = checkpointRepo.findById(checkpointId)
      .orElseThrow(() -> new RuntimeException("Không tìm thấy checkpoint: " + checkpointId));

    Team team = getTeam(cp.getTeamId());

    // leader hoặc lecturer (demo) được mark done
    boolean isLeader = team.getLeaderId() != null && team.getLeaderId().equals(ctx.getUserId());
    if (!(isLeader || ctx.isLecturer())) {
      throw new RuntimeException("Chỉ trưởng nhóm hoặc giảng viên mới được đánh dấu DONE");
    }

    cp.setStatus(CheckpointStatus.DONE);
    return checkpointRepo.save(cp);
  }

  @Transactional(readOnly = true)
  public List<CheckpointSubmission> getSubmissions(String checkpointId) {
    return submissionRepo.findByCheckpointIdOrderBySubmittedAtDesc(checkpointId);
  }
}
