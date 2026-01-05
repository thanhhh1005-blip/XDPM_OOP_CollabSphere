package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.dto.response.ActivityResponse;
import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationActivity;
import com.collab.collaborationservice.repository.CollaborationActivityRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationActivityServiceImpl implements CollaborationActivityService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationActivityRepository activityRepository;

    // ===================== LOG ACTIVITY =====================
    @Override
    public void logActivity(Long collaborationId,
                            String action,
                            Long actorId,
                            String description) {

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found"));

        CollaborationActivity activity = CollaborationActivity.builder()
                .collaboration(collaboration)
                .action(action)
                .actorId(actorId)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        activityRepository.save(activity);
    }

    // ===================== GET ACTIVITY HISTORY =====================
    @Override
    public List<ActivityResponse> getActivityHistory(Long collaborationId) {

        return activityRepository
                .findByCollaborationIdOrderByCreatedAtDesc(collaborationId)
                .stream()
                .map(activity -> ActivityResponse.builder()
                        .action(activity.getAction())
                        .actorId(activity.getActorId())
                        .description(activity.getDescription())
                        .createdAt(activity.getCreatedAt())
                        .build())
                .toList();
    }
}
