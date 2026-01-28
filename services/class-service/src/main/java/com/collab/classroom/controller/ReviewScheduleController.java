package com.collab.classroom.controller;

import com.collab.classroom.entity.ReviewSchedule;
import com.collab.classroom.repository.ReviewScheduleRepository;
import com.collab.classroom.service.ReviewScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.collab.classroom.client.TeamClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ReviewScheduleController {

    private final ReviewScheduleService service;
    private final ReviewScheduleRepository repository;
    private final TeamClient teamClient; 
    // 1. CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReviewSchedule schedule) {
        return ResponseEntity.ok(service.createSchedule(schedule));
    }

    // 2. READ (Lấy danh sách theo lớp)
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Map<String, Object>>> getByClass(@PathVariable Long classId) {
        
        List<ReviewSchedule> schedules = repository.findByClassId(classId);

        List<Map<String, Object>> result = schedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", schedule.getId());
            map.put("startTime", schedule.getStartTime());
            map.put("endTime", schedule.getEndTime());
            map.put("location", schedule.getLocation());
            map.put("type", schedule.getType());
            map.put("teamId", schedule.getTeamId());
            
          
            String displayName = schedule.getTitle(); 

          
            if ("TEAM".equals(schedule.getType()) && schedule.getTeamId() != null) {
                try {
                    
                     String teamName = teamClient.getTeamName(schedule.getTeamId()); 
                     displayName = teamName + " - " + schedule.getTitle();
                } catch (Exception e) {
                    System.err.println("Lỗi lấy tên team: " + e.getMessage());
                }
            }
            
            map.put("title", displayName); 

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 3. UPDATE (Sửa lịch)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("<id>") Long id, @RequestBody ReviewSchedule req) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(req.getTitle()); // Bạn nhớ thêm field Title trong Entity nhé
            existing.setStartTime(req.getStartTime());
            existing.setEndTime(req.getEndTime());
            existing.setLocation(req.getLocation());
            existing.setType(req.getType());
            existing.setTeamId(req.getTeamId());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. DELETE (Xóa lịch)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    
}