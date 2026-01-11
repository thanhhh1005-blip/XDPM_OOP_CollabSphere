package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.entity.Checkpoint;
import com.collab.workspaceservice.repository.MilestoneRepository;
import com.collab.workspaceservice.repository.CheckpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/milestones")
public class MilestoneController {

    @Autowired
    private MilestoneRepository milestoneRepo;

    @Autowired
    private CheckpointRepository checkpointRepo;

    // 1. Lấy tất cả cột mốc dự án
    @GetMapping
    public ApiResponse<Iterable<Milestone>> getAll() {
        return new ApiResponse<>(true, "Lấy danh sách Milestone thành công", milestoneRepo.findAll());
    }

    // 2. Tạo Milestone mới (Giảng viên dùng)
    @PostMapping
    public ApiResponse<Milestone> create(@RequestBody Milestone milestone) {
        return new ApiResponse<>(true, "Tạo cột mốc thành công", milestoneRepo.save(milestone));
    }

    // 3. Tạo Checkpoint cho Milestone
    @PostMapping("/{milestoneId}/checkpoints")
    public ApiResponse<Checkpoint> addCheckpoint(@PathVariable("milestoneId") Long milestoneId, @RequestBody Checkpoint cp) {
        Milestone ms = milestoneRepo.findById(milestoneId).orElse(null);
        if (ms == null) return new ApiResponse<>(false, "Không tìm thấy Milestone", null);
        cp.setMilestone(ms);
        return new ApiResponse<>(true, "Tạo Checkpoint thành công", checkpointRepo.save(cp));
    }

    // 4. Sinh viên nộp bài cho Checkpoint (Cập nhật URL nộp bài)
    @PutMapping("/checkpoints/{cpId}/submit")
    public ApiResponse<Checkpoint> submitCheckpoint(@PathVariable("cpId") Long cpId, @RequestParam("url") String url) {
        Checkpoint cp = checkpointRepo.findById(cpId).orElse(null);
        if (cp == null) return new ApiResponse<>(false, "Không tìm thấy Checkpoint", null);
        
        cp.setSubmissionUrl(url);
        cp.setStatus("SUBMITTED"); // Đổi trạng thái sang Đã nộp
        return new ApiResponse<>(true, "Nộp bài thành công!", checkpointRepo.save(cp));
    }
}