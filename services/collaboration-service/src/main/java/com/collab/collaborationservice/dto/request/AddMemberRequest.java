package com.collab.collaborationservice.dto.request;

import com.collab.collaborationservice.enums.CollaborationRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotNull
    private Long collaborationId;

    @NotNull
    private Long userId;

    @NotNull
    private CollaborationRole role;
}
