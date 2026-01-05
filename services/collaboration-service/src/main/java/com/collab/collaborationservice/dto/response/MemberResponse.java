package com.collab.collaborationservice.dto.response;

import com.collab.collaborationservice.enums.CollaborationRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponse {

    private Long userId;
    private CollaborationRole role;
    private boolean active;
}
