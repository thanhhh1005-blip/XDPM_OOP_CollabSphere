package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.entity.CollaborationActivity;
import com.collab.collaborationservice.repository.CollaborationActivityRepository;
import com.collab.collaborationservice.service.CollaborationActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollaborationActivityServiceImpl
        implements CollaborationActivityService {

    private final CollaborationActivityRepository activityRepository;

    @Override
    public void log(
            Long collaborationId,
            String action,
            String actor,
            String description
    ) {
        activityRepository.save(
                CollaborationActivity.builder()
                        .collaborationId(collaborationId)
                        .action(action)
                        .actor(actor)
                        .description(description)
                        .build()
        );
    }
}
