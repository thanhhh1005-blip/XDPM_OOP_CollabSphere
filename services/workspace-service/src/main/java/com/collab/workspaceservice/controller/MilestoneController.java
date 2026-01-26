package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Checkpoint;
import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.entity.SubTask;
import com.collab.workspaceservice.repository.CheckpointRepository;
import com.collab.workspaceservice.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.collab.workspaceservice.service.FileStorageService; // Nhớ import dòng này
import org.springframework.web.multipart.MultipartFile;
import com.collab.workspaceservice.repository.SubTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/workspace/milestones")
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;
    
    @Autowired 
    private CheckpointRepository checkpointRepo; 
    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired
    private FileStorageService fileStorageService;
    // ✅ SỬA LỖI 1: Thêm ("classId") vào đây
    @GetMapping("/class/{classId}")
    public ApiResponse<List<Milestone>> getByClass(@PathVariable("classId") Long classId) {
        return new ApiResponse<>(1000, "Thành công", milestoneService.getByClassId(classId));
    }

    @PostMapping
    public ApiResponse<Milestone> createManual(@RequestBody Milestone milestone) {
        return new ApiResponse<>(1000, "Tạo thành công", milestoneService.createManual(milestone));
    }

    // ✅ SỬA LỖI 2: Thêm ("classId") vào đây nếu dùng
    @PostMapping("/ai-generate")
    public ApiResponse<List<Milestone>> createByAI(
            @RequestParam("classId") Long classId,
            @RequestParam(value = "description", required = false) String description
    ) {
        return new ApiResponse<>(1000, "AI đã tạo lộ trình", milestoneService.generateByAI(classId, description));
    }
    
    // ✅ SỬA LỖI 3: Thêm ("id")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        milestoneService.delete(id);
        return new ApiResponse<>(1000, "Đã xóa", null);
    }

    // --- PHẦN CHECKPOINT (NỘP BÀI) ---

    @PostMapping("/checkpoint/submit")
    public ApiResponse<Checkpoint> submitCheckpoint(@RequestBody Checkpoint req) {
        // Kiểm tra xem nhóm đã nộp bài này chưa
        Checkpoint existing = checkpointRepo.findByMilestoneIdAndTeamId(req.getMilestoneId(), req.getTeamId());
        
        if (existing != null) {
            // Nếu nộp rồi thì cập nhật lại link mới
            existing.setSubmissionUrl(req.getSubmissionUrl());
            existing.setNote(req.getNote());
            existing.setStatus("SUBMITTED");
            existing.setSubmittedAt(LocalDateTime.now());
            return new ApiResponse<>(1000, "Cập nhật bài nộp thành công", checkpointRepo.save(existing));
        }

        // Nếu chưa thì tạo mới
        req.setStatus("SUBMITTED");
        req.setSubmittedAt(LocalDateTime.now());
        return new ApiResponse<>(1000, "Nộp bài thành công", checkpointRepo.save(req));
    }

    // 2. API: GIẢNG VIÊN XEM DANH SÁCH NỘP
    @GetMapping("/{id}/checkpoints")
    public ApiResponse<List<Checkpoint>> getCheckpointsByMilestone(@PathVariable("id") Long milestoneId) {
        return new ApiResponse<>(1000, "Thành công", checkpointRepo.findByMilestoneId(milestoneId));
    }

    // 3. API: LẤY TRẠNG THÁI (Để tô màu xanh cho SV)
    @GetMapping("/checkpoint/status")
    public ApiResponse<List<Checkpoint>> getCheckpointStatus(@RequestParam("teamId") String teamId) {
        return new ApiResponse<>(1000, "Thành công", checkpointRepo.findByTeamId(teamId));
    }

    // API NỘP BÀI CÓ FILE (QUAN TRỌNG)
    @PostMapping(value = "/checkpoint/submit-file", consumes = {"multipart/form-data"})
    public ApiResponse<Checkpoint> submitCheckpointWithFile(
            @RequestParam("milestoneId") Long milestoneId,
            @RequestParam("teamId") String teamId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "file", required = false) MultipartFile file // Nhận file từ React
    ) {
        String fileUrl = null;
        
        // 1. Nếu có file -> Gọi Service để upload lên MinIO
        if (file != null && !file.isEmpty()) {
            fileUrl = fileStorageService.storeFile(file);
        }

        // 2. Logic lưu Checkpoint (Giống hệt nộp link cũ)
        Checkpoint existing = checkpointRepo.findByMilestoneIdAndTeamId(milestoneId, teamId);
        
        if (existing != null) {
            if (fileUrl != null) existing.setSubmissionUrl(fileUrl); // Cập nhật link file mới
            existing.setNote(note);
            existing.setStatus("SUBMITTED");
            existing.setSubmittedAt(LocalDateTime.now());
            return new ApiResponse<>(1000, "Cập nhật bài nộp thành công", checkpointRepo.save(existing));
        }

        // Tạo mới nếu chưa nộp lần nào
        Checkpoint newCp = Checkpoint.builder()
                .milestoneId(milestoneId)
                .teamId(teamId)
                .submissionUrl(fileUrl)
                .note(note)
                .status("SUBMITTED")
                .submittedAt(LocalDateTime.now())
                .build();
                
        return new ApiResponse<>(1000, "Nộp bài thành công", checkpointRepo.save(newCp));
    }
    
    @PostMapping("/batch-save")
    public ApiResponse<List<Milestone>> saveAllMilestones(
            @RequestParam("classId") Long classId,
            @RequestBody List<Milestone> milestones
    ) {
        // Gán classId cho tất cả milestone trước khi lưu
        milestones.forEach(m -> m.setClassId(classId));
        
        // Gọi service lưu (Hàm saveAll bạn đã thêm ở bước trước)
        return new ApiResponse<>(1000, "Lưu lộ trình thành công", milestoneService.saveAll(milestones));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Milestone> update(
            @PathVariable Long id,
            @RequestBody Milestone req
    ) {
        // Tìm milestone cũ
        Milestone existing = milestoneService.getById(id); // Đảm bảo Service có hàm getById
        if (existing == null) throw new RuntimeException("Không tìm thấy Milestone");

        // Cập nhật thông tin
        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setWeekNumber(req.getWeekNumber());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        
        // Lưu lại
        return new ApiResponse<>(1000, "Cập nhật thành công", milestoneService.save(existing)); 
        // Lưu ý: Service cần có hàm save (bạn có thể dùng lại repo.save)
    }

    // File: MilestoneController.java

    @PostMapping("/complete/{id}")
    public ApiResponse<Checkpoint> completeMilestone(
            @PathVariable("id") Long milestoneId,
            @RequestParam("teamId") String teamId
    ) {
        Checkpoint cp = checkpointRepo.findByMilestoneIdAndTeamId(milestoneId, teamId);
        
        // 1. Nếu chưa có record checkpoint nào -> Tạo mới (Mặc định là chưa xong)
        if (cp == null) {
            cp = Checkpoint.builder()
                    .milestoneId(milestoneId)
                    .teamId(teamId)
                    .status("IN_PROGRESS") 
                    .build();
        }

        // 2. LOGIC TOGGLE (BẬT/TẮT)
        if ("COMPLETED".equals(cp.getStatus())) {
            // A. Nếu đang HOÀN THÀNH -> Cho phép HỦY (Undo)
            cp.setStatus("IN_PROGRESS");
            return new ApiResponse<>(1000, "Đã hủy trạng thái hoàn thành.", checkpointRepo.save(cp));
        } else {
            // B. Nếu chưa hoàn thành -> Kiểm tra điều kiện để HOÀN THÀNH
            List<SubTask> tasks = subTaskRepository.findByMilestoneIdAndTeamId(milestoneId, teamId);
            
            if (tasks.isEmpty()) {
                 throw new RuntimeException("Cần tạo ít nhất 1 đầu việc (checkpoint) trước khi hoàn thành!");
            }

            boolean allDone = tasks.stream().allMatch(SubTask::isCompleted);
            if (!allDone) {
                throw new RuntimeException("Vẫn còn công việc chưa xong! Không thể hoàn thành.");
            }

            cp.setStatus("COMPLETED");
            cp.setSubmittedAt(LocalDateTime.now());
            return new ApiResponse<>(1000, "Đã hoàn thành giai đoạn xuất sắc!", checkpointRepo.save(cp));
        }
    }
}