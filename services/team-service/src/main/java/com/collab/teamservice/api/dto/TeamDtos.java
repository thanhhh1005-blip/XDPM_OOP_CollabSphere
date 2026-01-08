package com.collab.teamservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamDtos {

  public record CreateTeamReq(
    @NotBlank String classId,
    @NotBlank String name
  ) {}

  public record AddMemberReq(
    @NotBlank String userId
  ) {}

  public record CreateMilestoneReq(
    @NotBlank String title,
    String description,
    Integer orderNo
  ) {}

  public record CreateCheckpointReq(
    String milestoneId,
    @NotBlank String title,
    String description,
    String assigneeId
  ) {}

  public record SubmitCheckpointReq(
    @NotBlank String content
  ) {}
}
