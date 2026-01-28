package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.dto.AiGenerateRequest; // Import DTO vừa tạo
import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/milestones") 
@RequiredArgsConstructor
public class AiMilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping("/generate-and-save")
    public ApiResponse<List<Milestone>> generateAndSave(@RequestBody AiGenerateRequest request) {
        
        List<Milestone> result = milestoneService.generateFromSyllabus(
                request.getClassId(),
                request.getSyllabusContent(),
                request.getDurationWeeks()
        );

        return new ApiResponse<>(1000, "AI tạo thành công", result);
    }
}