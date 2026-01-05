package com.collab.collaborationservice.service.authorization;

import com.collab.collaborationservice.enums.CollaborationRole;

public interface CollaborationAuthorizationService {

    // Validate user thuộc collaboration
    CollaborationRole validateMember(Long collaborationId, Long userId);

    // Validate quyền quản lý (leader)
    void validateLeader(Long collaborationId, Long userId);

    // Validate quyền chia sẻ
    void validateSharePermission(Long collaborationId, Long userId);

    // Validate quyền xem
    void validateViewPermission(Long collaborationId, Long userId);
}
