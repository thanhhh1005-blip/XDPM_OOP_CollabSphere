package com.collab.classroom.service;

import com.collab.classroom.client.TeamClient;
import com.collab.classroom.entity.ReviewSchedule;
import com.collab.classroom.repository.ReviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewScheduleService {

    private final ReviewScheduleRepository repository;
    private final TeamClient teamClient; // Inject client để lấy tên team

    // 1. Hàm tạo lịch (Cho Staff)
    public ReviewSchedule createSchedule(ReviewSchedule schedule) {
        return repository.save(schedule);
    }

    // 2. Hàm lấy lịch hiển thị (Cho Giảng viên/SV)
    public List<Map<String, Object>> getSchedulesForView(Long classId) {
        List<ReviewSchedule> schedules = repository.findByClassId(classId);

        return schedules.stream().map(s -> {
            Map<String, Object> event = new HashMap<>();
            event.put("id", s.getId());
            event.put("start", s.getStartTime());
            event.put("end", s.getEndTime());
            
            if ("TEAM".equals(s.getType())) {
                String teamName = "Unknown Team";
                try {
                    if(s.getTeamId() != null) teamName = teamClient.getTeamName(s.getTeamId());
                } catch (Exception e) { teamName = "Team " + s.getTeamId(); }

                event.put("title", "TEAM: " + teamName + " - ID: " + s.getTeamId());
                event.put("backgroundColor", "#10b981"); 
                event.put("borderColor", "#10b981");
            } else {
                // Lịch lớp
                event.put("title", "LỚP Chung - ID: " + s.getClassId());
                event.put("backgroundColor", "#f97316"); 
                event.put("borderColor", "#f97316");
            }

            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("location", s.getLocation());
            extendedProps.put("teamId", s.getTeamId());
            extendedProps.put("classId", s.getClassId());
            event.put("extendedProps", extendedProps);

            return event;
        }).collect(Collectors.toList());
    }
}