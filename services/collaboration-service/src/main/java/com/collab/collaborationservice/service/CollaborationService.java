package com.collab.collaborationservice.service;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;
import com.collab.collaborationservice.entity.Collaboration;

import java.util.List;

public interface CollaborationService {

    Collaboration createCollaboration(
            String name,
            String description,
            String createdBy
    );

    List<Collaboration> getMyCollaborations(String userId);

    Collaboration getById(Long collaborationId);

    // 1. Tạo mới
    CollaborationResponse create(CreateCollaborationRequest request);

    // 2. Lấy chi tiết
    CollaborationResponse getDetail(Long id);

    // 3. Lấy theo User
    List<CollaborationResponse> getByUser(Long userId);

    // 4. Đóng Collaboration
    void close(Long id, Long requesterId);
}
