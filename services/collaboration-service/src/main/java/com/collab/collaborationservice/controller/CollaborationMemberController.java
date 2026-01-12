package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.AddMemberRequest;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.service.CollaborationMemberService;
import com.collab.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations/{collaborationId}/members")
@RequiredArgsConstructor
public class CollaborationMemberController {

    private final CollaborationMemberService memberService;

    // Thêm thành viên
    @PostMapping
    public ApiResponse<Void> addMember(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable Long collaborationId,
            @RequestBody AddMemberRequest request
    ) {
        // Chuyển String userId sang Long
        memberService.addMember(collaborationId, request, Long.parseLong(userId));
        return new ApiResponse<>(true, "Member added successfully", null);
    }

    // Xóa thành viên
    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> removeMember(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable Long collaborationId,
            @PathVariable Long memberId
    ) {
        memberService.removeMember(collaborationId, memberId, Long.parseLong(userId));
        return new ApiResponse<>(true, "Member removed successfully", null);
    }

    // Danh sách thành viên
    @GetMapping
    public ApiResponse<List<MemberResponse>> listMembers(@PathVariable Long collaborationId) {
        return new ApiResponse<>(true, "Success", memberService.listMembers(collaborationId));
    }
}