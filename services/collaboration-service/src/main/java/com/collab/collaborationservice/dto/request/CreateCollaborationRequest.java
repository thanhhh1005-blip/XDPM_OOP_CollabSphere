package com.collab.collaborationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCollaborationRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long createdBy; // userId (tạm thời, sau có thể lấy từ JWT)

    @NotNull
    private Long teamId;
}
