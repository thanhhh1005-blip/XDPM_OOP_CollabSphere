package com.collab.communicationservice.controller;

import com.collab.communicationservice.entity.Meeting;
import com.collab.communicationservice.repository.MeetingRepository;
import com.collab.shared.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat/meetings")
public class MeetingController {

    @Autowired 
    private MeetingRepository meetingRepo;

    @PostMapping("/{roomId}/start")
    public ApiResponse<Meeting> startMeeting(
      @PathVariable("roomId") Long roomId,
      @RequestParam("hostName") String hostName,
      @RequestParam("password") String password // 👈 Nhận pass từ Giảng viên
      ) {
      Meeting m = new Meeting();
      m.setRoomId(roomId);
      m.setHostName(hostName);
      m.setStartTime(LocalDateTime.now());
      m.setPassword(password); // 👈 Lưu mật khẩu vào DB
      return new ApiResponse<>(true, "Meeting Started", meetingRepo.save(m));
    }

    @GetMapping("/{roomId}/status")
    public ApiResponse<Meeting> getStatus(
        @PathVariable("roomId") Long roomId        // 👈 PHẢI THÊM ("roomId")
    ) {
        return new ApiResponse<>(true, "Status", meetingRepo.findById(roomId).orElse(null));
    }

    @DeleteMapping("/{roomId}/end")
    public ApiResponse<Void> endMeeting(
        @PathVariable("roomId") Long roomId        // 👈 PHẢI THÊM ("roomId")
    ) {
        meetingRepo.deleteById(roomId);
        return new ApiResponse<>(true, "Meeting Ended", null);
    }
}