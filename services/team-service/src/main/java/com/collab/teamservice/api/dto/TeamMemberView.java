package com.collab.teamservice.api.dto;

public record TeamMemberView(
    String userId,
    String fullName,
    String memberRole,
    Double contributionPercent
) {}
