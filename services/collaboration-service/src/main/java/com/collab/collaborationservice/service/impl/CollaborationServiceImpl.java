package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.enums.CollaborationStatus;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;

    // ===================== CREATE =====================
    @Override
    public CollaborationResponse create(CreateCollaborationRequest request) {
        // BƯỚC 1: Tạo đối tượng Collaboration từ Request trước
        Collaboration collaboration = Collaboration.builder()
                .name(request.getName())
                .description(request.getDescription())
                .teamId(request.getTeamId())
                .createdBy(request.getCreatedBy())
                .status(CollaborationStatus.ACTIVE) // Hoặc giá trị Enum tương ứng
                .build();

        // BƯỚC 2: Lưu Collaboration vào Database để lấy ID
        Collaboration saved = collaborationRepository.save(collaboration);

        // BƯỚC 3: Người tạo mặc định là OWNER
        CollaborationMember owner = CollaborationMember.builder()
                .collaboration(saved)
                .userId(request.getCreatedBy())
                .role(CollaborationRole.OWNER)
                .active(true)
                .build();
        memberRepository.save(owner);

        // BƯỚC 4: Trả về kết quả (Dùng hàm mapToResponse mình đã sửa lúc nãy)
        return mapToResponse(saved);
    }

    // ===================== DETAIL =====================
    @Override
    @Transactional(readOnly = true)
    public CollaborationResponse getDetail(Long collaborationId) {

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Collaboration not found: " + collaborationId));

        return mapToResponse(collaboration);
    }

    // ===================== LIST BY USER =====================
    @Override
    @Transactional(readOnly = true)
    public List<CollaborationResponse> getByUser(Long userId) {

        return collaborationRepository.findByMemberUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===================== CLOSE =====================
    @Override
    public void close(Long collaborationId, Long requesterId) {

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Collaboration not found: " + collaborationId));

        CollaborationMember member = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, requesterId)
                .orElseThrow(() ->
                        new IllegalStateException("User is not a collaboration member"));

        if (member.getRole() != CollaborationRole.OWNER) {
            throw new IllegalStateException("Only OWNER can close collaboration");
        }

        collaboration.setStatus(CollaborationStatus.CLOSED);
        collaborationRepository.save(collaboration);
    }

    // ===================== MAPPER =====================
    private CollaborationResponse mapToResponse(Collaboration collaboration) {

        List<MemberResponse> members = memberRepository
                .findByCollaborationId(collaboration.getId())
                .stream()
                .map(member -> MemberResponse.builder()
                        .userId(member.getUserId())
                        .role(member.getRole())
                        .active(member.isActive())
                        .build())
                .toList();

        return CollaborationResponse.builder()
                .id(collaboration.getId())
                .name(collaboration.getName())
                .description(collaboration.getDescription())
                .status(collaboration.getStatus())
                .teamId(collaboration.getTeamId())
                .createdBy(collaboration.getCreatedBy())
                .createdAt(collaboration.getCreatedAt())
                .members(members)
                .build();
    }

        @Override
        public Collaboration createCollaboration(String name, String description, String createdBy) {
        // 1. Chuyển String userId sang Long
        Long userId = Long.parseLong(createdBy);

        // 2. Tạo đối tượng Collaboration
        Collaboration collaboration = Collaboration.builder()
            .name(name)
            .description(description)
            .createdBy(userId)
            .status(CollaborationStatus.ACTIVE) // Mặc định là ACTIVE
            // .createdAt(LocalDateTime.now()) // Nếu trong Entity chưa có @PrePersist
            .build();

        // 3. Lưu Collaboration
        Collaboration saved = collaborationRepository.save(collaboration);

        // 4. Thêm người tạo vào làm OWNER
        CollaborationMember owner = CollaborationMember.builder()
            .collaboration(saved)
            .userId(userId)
            .role(CollaborationRole.OWNER)
            .active(true)
            .build();
    
        memberRepository.save(owner);

        return saved;
        }

        @Override
        public List<Collaboration> getMyCollaborations(String userId) {
                // 1. Chuyển String sang Long
                Long uid = Long.parseLong(userId);

                // 2. Gọi Repository (Hàm này mình đã thêm ở các bước trước)
                return collaborationRepository.findByMemberUserId(uid);
        }

        @Override
        public Collaboration getById(Long collaborationId) {
                return collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found with id: " + collaborationId));
        }
}
