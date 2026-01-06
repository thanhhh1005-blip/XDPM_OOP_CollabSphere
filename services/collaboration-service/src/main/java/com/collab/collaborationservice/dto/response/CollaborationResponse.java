package com.collab.collaborationservice.dto.response;

import com.collab.collaborationservice.enums.CollaborationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CollaborationResponse {

    private Long id;
    private String name;
    private String description;
    private CollaborationStatus status;

    private Long teamId;
    private Long createdBy;

    private LocalDateTime createdAt;

    private List<MemberResponse> members;
}
