package com.collabsphere.aiservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.MilestoneGenRequest;
import com.collab.shared.dto.MilestoneGenResponse;
import com.collabsphere.aiservice.service.MilestoneGeneratorService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai/milestones")
@RequiredArgsConstructor
public class AiMilestoneController {

    private final MilestoneGeneratorService generatorService;
    
    @PostMapping("/generate-and-save")
    public ApiResponse<List<MilestoneGenResponse>> generateAndSave(@RequestBody MilestoneGenRequest request) {
        
        // 1. Nhờ AI tạo nội dung
        List<MilestoneGenResponse> milestonesData = generatorService.generateFromSyllabus(
                request.getSyllabusContent(), 
                request.getDurationWeeks()
        );

        // 2. Gửi sang Workspace Service để lưu (Tạm thời return luôn để test AI trước)

        return new ApiResponse<>(1000, "Đã tạo Milestones thành công", milestonesData);
    }
}