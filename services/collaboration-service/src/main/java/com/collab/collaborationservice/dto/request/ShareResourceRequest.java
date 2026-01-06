package com.collab.collaborationservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareResourceRequest {

    @NotNull
    private Long collaborationId;

    @NotNull
    private Long resourceId;

    @NotNull
    private Long sharedBy; // userId
}
