package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.enums.CollaborationStatus;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationActivityService;
import com.collab.collaborationservice.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;
    private final CollaborationActivityService activityService;

    @Override
    public Collaboration createCollaboration(
            String name,
            String description,
            String createdBy
    ) {

        Collaboration collaboration = Collaboration.builder()
                .name(name)
                .description(description)
                .createdBy(createdBy)
                .status(CollaborationStatus.ACTIVE)
                .build();

        Collaboration saved = collaborationRepository.save(collaboration);

        // Người tạo = OWNER
        memberRepository.save(
                CollaborationMember.builder()
                        .collaboration(saved)
                        .userId(createdBy)
                        .role(CollaborationRole.OWNER)
                        .build()
        );

        activityService.log(
                saved.getId(),
                "CREATE",
                createdBy,
                "Created collaboration"
        );

        return saved;
    }

    @Override
    public List<Collaboration> getMyCollaborations(String userId) {
        return collaborationRepository.findByCreatedBy(userId);
    }

    @Override
    public Collaboration getById(Long collaborationId) {
        return collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found"));
    }
}
