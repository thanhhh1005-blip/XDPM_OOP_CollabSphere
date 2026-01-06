package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.AddMemberRequest;
import com.collab.collaborationservice.dto.response.ApiResponse;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.service.CollaborationMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations/{collaborationId}/members")
@RequiredArgsConstructor
public class CollaborationMemberController {

    private final CollaborationMemberService memberService;

    @PostMapping
    public ApiResponse<Void> addMember(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable Long collaborationId,
            @RequestBody AddMemberRequest request
    ) {
        memberService.addMember(collaborationId, userId, role, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> removeMember(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable Long collaborationId,
            @PathVariable String memberId
    ) {
        memberService.removeMember(collaborationId, userId, memberId);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<List<MemberResponse>> listMembers(
            @PathVariable Long collaborationId
    ) {
        return ApiResponse.success(
                memberService.listMembers(collaborationId)
        );
    }
}
