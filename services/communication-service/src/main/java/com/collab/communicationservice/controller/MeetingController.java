package com.collab.communicationservice.controller;

import com.collab.communicationservice.entity.ChatMessage;
import com.collab.communicationservice.entity.Meeting;
import com.collab.communicationservice.repository.MeetingRepository;
import com.collab.shared.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat/meetings")
public class MeetingController {

    @Autowired 
    private MeetingRepository meetingRepo;

   @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/{roomId}/start")
    public ApiResponse<Meeting> startMeeting(
        @PathVariable("roomId") String roomId,
        @RequestParam("hostName") String hostName,
        @RequestParam("password") String password
    ) {
        Meeting m = new Meeting();
        m.setRoomId(roomId);
        m.setHostName(hostName);
        m.setStartTime(LocalDateTime.now());
        m.setPassword(password);
        
        Meeting savedMeeting = meetingRepo.save(m);

        // 3. THÊM ĐOẠN NÀY: Bắn tín hiệu "CALL_START" qua Socket
        ChatMessage sysMsg = new ChatMessage();
        sysMsg.setRoomId(roomId);
        sysMsg.setSenderName("HỆ THỐNG");
        sysMsg.setContent("Giảng viên đã mở cuộc họp.");
        sysMsg.setType("CALL_START"); // Tín hiệu đặc biệt
        sysMsg.setTimestamp(LocalDateTime.now());
        
        // Gửi tới tất cả người đang xem phòng này
        messagingTemplate.convertAndSend("/topic/room/" + roomId, sysMsg);

        return new ApiResponse<>(1000, "Meeting Started", savedMeeting);
    }

    @GetMapping("/{roomId}/status")
    public ApiResponse<Meeting> getStatus(
        @PathVariable("roomId") String roomId        // 👈 PHẢI THÊM ("roomId")
    ) {
        return new ApiResponse<>(1000, "Status", meetingRepo.findById(roomId).orElse(null));
    }

    @DeleteMapping("/{roomId}/end")
    public ApiResponse<Void> endMeeting(@PathVariable("roomId") String roomId) {
        meetingRepo.deleteById(roomId);

        // 4. THÊM ĐOẠN NÀY: Bắn tín hiệu "CALL_END" để máy SV tự tắt
        ChatMessage sysMsg = new ChatMessage();
        sysMsg.setRoomId(roomId);
        sysMsg.setSenderName("HỆ THỐNG");
        sysMsg.setContent("Cuộc họp đã kết thúc.");
        sysMsg.setType("CALL_END");
        
        messagingTemplate.convertAndSend("/topic/room/" + roomId, sysMsg);

        return new ApiResponse<>(1000, "Meeting Ended", null);
    }
}