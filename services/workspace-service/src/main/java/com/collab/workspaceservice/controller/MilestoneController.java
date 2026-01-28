package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.CheckpointDTO;
import com.collab.shared.dto.UserDTO;
import com.collab.workspaceservice.client.IdentityClient;
import com.collab.workspaceservice.entity.Checkpoint;
import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.entity.SubTask;
import com.collab.workspaceservice.repository.CheckpointRepository;
import com.collab.workspaceservice.service.MilestoneService;
import com.collab.workspaceservice.service.RabbitMQSender;
import com.collab.workspaceservice.client.TeamClient; // Import mới
import com.collab.workspaceservice.dto.TeamResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.collab.workspaceservice.service.FileStorageService; // Nhớ import dòng này
import org.springframework.web.multipart.MultipartFile;
import com.collab.workspaceservice.repository.SubTaskRepository;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
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

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private IdentityClient identityClient;

    @Autowired 
    private TeamClient teamClient;
    @GetMapping("/class/{classId}")
    public ApiResponse<List<Milestone>> getByClass(@PathVariable("classId") Long classId) {
        return new ApiResponse<>(1000, "Thành công", milestoneService.getByClassId(classId));
    }

    @PostMapping
    public ApiResponse<Milestone> createManual(@RequestBody Milestone milestone) {
        return new ApiResponse<>(1000, "Tạo thành công", milestoneService.createManual(milestone));
    }

    @PostMapping("/ai-generate")
    public ApiResponse<List<Milestone>> createByAI(
            @RequestParam("classId") Long classId,
            @RequestParam(value = "description", required = false) String description
    ) {
        return new ApiResponse<>(1000, "AI đã tạo lộ trình", milestoneService.generateByAI(classId, description));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        milestoneService.delete(id);
        return new ApiResponse<>(1000, "Đã xóa", null);
    }


    private String getTeamName(String teamId) {
        try {
            TeamResponse team = teamClient.getTeamById(teamId);
            if (team != null && team.getName() != null) {
                return team.getName(); 
            }
        } catch (Exception e) {
            System.err.println("⚠️ Không gọi được TeamService: " + e.getMessage());
        }
        return "Nhóm " + teamId; 
    }

    @PostMapping("/checkpoint/submit")
    public ApiResponse<Checkpoint> submitCheckpoint(@RequestBody Checkpoint req) {
        Checkpoint existing = checkpointRepo.findByMilestoneIdAndTeamId(req.getMilestoneId(), req.getTeamId());
        Checkpoint savedCp;
        if (existing != null) {
            existing.setSubmissionUrl(req.getSubmissionUrl());
            existing.setNote(req.getNote());
            existing.setStatus("SUBMITTED");
            existing.setSubmittedAt(LocalDateTime.now());
            savedCp = checkpointRepo.save(existing);
        } else {
            req.setStatus("SUBMITTED");
            req.setSubmittedAt(LocalDateTime.now());
            savedCp = checkpointRepo.save(req);
        }

        String teamName = getTeamName(req.getTeamId());
        sendNotificationToTeacher(teamName, "vừa nộp bài (Link)", req.getNote()); // Truyền teamName vào

        return new ApiResponse<>(1000, "Nộp bài thành công", savedCp);
    }

    @GetMapping("/{id}/checkpoints")
    public ApiResponse<List<CheckpointDTO>> getCheckpoints(
            @PathVariable("id") Long id,
            @RequestParam(value = "teamId", required = false) String teamId
    ) {
        List<Checkpoint> entities;
        if (teamId != null && !teamId.isEmpty()) {
            Checkpoint cp = checkpointRepo.findByMilestoneIdAndTeamId(id, teamId);
            entities = (cp != null) ? List.of(cp) : List.of();
        } else {
            entities = checkpointRepo.findByMilestoneId(id);
        }

        List<CheckpointDTO> dtos = entities.stream().map(cp -> {
            return CheckpointDTO.builder()
                    .id(cp.getId())
                    .milestoneId(cp.getMilestoneId())
                    .teamId(cp.getTeamId())
                    
                    .teamName(getTeamName(cp.getTeamId())) 
                    
                    .status(cp.getStatus())
                    .submissionUrl(cp.getSubmissionUrl())
                    .score(cp.getScore())
                    .feedback(cp.getFeedback())
                    .note(cp.getNote())
                    .submittedAt(cp.getSubmittedAt())
                    .build();
        }).collect(Collectors.toList());

        return new ApiResponse<>(1000, "Lấy danh sách thành công", dtos);
    }

    @GetMapping("/checkpoint/status")
    public ApiResponse<List<Checkpoint>> getCheckpointStatus(@RequestParam("teamId") String teamId) {
        return new ApiResponse<>(1000, "Thành công", checkpointRepo.findByTeamId(teamId));
    }

    @PostMapping(value = "/checkpoint/submit-file", consumes = {"multipart/form-data"})
    public ApiResponse<Checkpoint> submitCheckpointWithFile(
            @RequestParam("milestoneId") Long milestoneId,
            @RequestParam("teamId") String teamId,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = fileStorageService.storeFile(file);
        }

        Checkpoint existing = checkpointRepo.findByMilestoneIdAndTeamId(milestoneId, teamId);
        Checkpoint savedCp;

        if (existing != null) {
            if (fileUrl != null) existing.setSubmissionUrl(fileUrl);
            existing.setNote(note);
            existing.setStatus("SUBMITTED");
            existing.setSubmittedAt(LocalDateTime.now());
            savedCp = checkpointRepo.save(existing);
        } else {
            Checkpoint newCp = Checkpoint.builder()
                    .milestoneId(milestoneId)
                    .teamId(teamId)
                    .submissionUrl(fileUrl)
                    .note(note)
                    .status("SUBMITTED")
                    .submittedAt(LocalDateTime.now())
                    .build();
            savedCp = checkpointRepo.save(newCp);
        }

        // GỌI HÀM GỬI MAIL
        String teamName = getTeamName(teamId);
        sendNotificationToTeacher(teamName, "vừa nộp bài (File)", note);
        return new ApiResponse<>(1000, "Nộp bài thành công", savedCp);
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

        Milestone existing = milestoneService.getById(id); // Đảm bảo Service có hàm getById
        if (existing == null) throw new RuntimeException("Không tìm thấy Milestone");


        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setWeekNumber(req.getWeekNumber());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());

        return new ApiResponse<>(1000, "Cập nhật thành công", milestoneService.save(existing)); 
        
    }


    @PostMapping("/complete/{id}")
    public ApiResponse<Checkpoint> completeMilestone(
            @PathVariable("id") Long milestoneId,
            @RequestParam("teamId") String teamId
    ) {
        Checkpoint cp = checkpointRepo.findByMilestoneIdAndTeamId(milestoneId, teamId);
        
        if (cp == null) {
            cp = Checkpoint.builder()
                    .milestoneId(milestoneId)
                    .teamId(teamId)
                    .status("IN_PROGRESS") 
                    .build();
        }

        if ("COMPLETED".equals(cp.getStatus())) {
            cp.setStatus("IN_PROGRESS");
            return new ApiResponse<>(1000, "Đã hủy trạng thái hoàn thành.", checkpointRepo.save(cp));
        } else {
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

    @PutMapping("/checkpoint/grade")
    public ApiResponse<Checkpoint> gradeCheckpoint(
        @RequestParam("milestoneId") Long milestoneId,
        @RequestParam("teamId") String teamId,
        @RequestBody Checkpoint gradeReq
    ) {
        Checkpoint cp = checkpointRepo.findByMilestoneIdAndTeamId(milestoneId, teamId);
        if (cp == null) throw new RuntimeException("Nhóm chưa nộp bài hoặc chưa bắt đầu!");
        
        cp.setScore(gradeReq.getScore());
        cp.setFeedback(gradeReq.getFeedback());
        
        try {
            String teamName = getTeamName(teamId); 
            
            String studentUsername = "student2"; 
            ApiResponse<UserDTO> response = identityClient.getUser(studentUsername);
            
            if (response != null && response.getResult() != null) {
                String emailSinhVien = response.getResult().getEmail();
                
                String subject = "📢 Kết quả chấm điểm cho " + teamName;
                String content = "<h3>Giảng viên đã chấm điểm!</h3>" +
                                 "<p>Nhóm: <b>" + teamName + "</b></p>" + // Hiện tên nhóm
                                 "<p>Điểm số: <b style='color:red'>" + gradeReq.getScore() + "</b></p>" +
                                 "<p>Nhận xét: " + gradeReq.getFeedback() + "</p>";

                rabbitMQSender.sendEmailNotification(emailSinhVien, subject, content);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi gửi mail cho SV: " + e.getMessage());
        }

        return new ApiResponse<>(1000, "Đã chấm điểm thành công", checkpointRepo.save(cp));
    }

    @GetMapping("/class/{classId}/stats")
    public ApiResponse<Map<Long, Long>> getMilestoneStats(@PathVariable("classId") Long classId) {
        List<Milestone> milestones = milestoneService.getByClassId(classId);
        if (milestones.isEmpty()) {
            return new ApiResponse<>(1000, "Thành công", new HashMap<>());
        }

        List<Long> ids = milestones.stream().map(Milestone::getId).collect(Collectors.toList());

        List<Object[]> counts = checkpointRepo.countSubmissionsByMilestoneIds(ids);

        Map<Long, Long> stats = new HashMap<>();
        for (Object[] row : counts) {
            stats.put((Long) row[0], (Long) row[1]);
        }

        return new ApiResponse<>(1000, "Lấy thống kê thành công", stats);
    }

    private void sendNotificationToTeacher(String teamNameOrId, String action, String note) {
        try {
            String teacherUsername = "giangvien";
            ApiResponse<UserDTO> response = identityClient.getUser(teacherUsername);

            if (response != null && response.getResult() != null) {
                String emailGiangVien = response.getResult().getEmail();
                
                String subject = "🔔 " + teamNameOrId + " " + action;
                String content = "<h3>Có hoạt động mới từ " + teamNameOrId + "</h3>" +
                                 "<p>Hành động: " + action + "</p>" +
                                 "<p>Ghi chú: " + (note != null ? note : "Không có") + "</p>";

                rabbitMQSender.sendEmailNotification(emailGiangVien, subject, content);
            }
        } catch (Exception e) {
             System.err.println("⚠️ Lỗi gửi mail cho GV: " + e.getMessage());
        }
    }
}