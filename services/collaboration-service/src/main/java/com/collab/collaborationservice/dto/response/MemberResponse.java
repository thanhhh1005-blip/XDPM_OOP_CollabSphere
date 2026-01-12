package com.collab.collaborationservice.dto.response;

import com.collab.collaborationservice.enums.CollaborationRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long userId;
    private CollaborationRole role;
    private boolean active;
}
