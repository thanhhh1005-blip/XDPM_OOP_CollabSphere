package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.entity.EditorData;
import com.collab.collaborationservice.entity.WhiteboardData;
import com.collab.collaborationservice.repository.EditorRepository;
import com.collab.collaborationservice.repository.WhiteboardRepository;
import com.collab.shared.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/collab")
public class CollabController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WhiteboardRepository whiteboardRepo;

    @Autowired
    private EditorRepository editorRepo;

    // --- 1. XỬ LÝ BẢNG VẼ (WHITEBOARD) ---

    // Nhận nét vẽ từ Socket và phát lại cho nhóm
    @MessageMapping("/whiteboard/{teamId}")
    public void syncDrawing(@DestinationVariable("teamId") String teamId, @Payload String data) {
        // Gửi cho các thành viên khác xem live
        messagingTemplate.convertAndSend("/topic/collab/whiteboard/" + teamId, data);
        
        // Lưu trữ vào bảng riêng của Whiteboard
        WhiteboardData wb = whiteboardRepo.findById(teamId).orElse(new WhiteboardData());
        wb.setTeamId(teamId);
        wb.setContent(data);
        wb.setUpdatedAt(LocalDateTime.now());
        whiteboardRepo.save(wb);
    }

    // API lấy dữ liệu bảng vẽ cũ khi vào phòng
    @GetMapping("/whiteboard/{teamId}")
    public ApiResponse<String> getWhiteboard(@PathVariable("teamId") String teamId) {
        String content = whiteboardRepo.findById(teamId)
                .map(WhiteboardData::getContent)
                .orElse("{}");
        return new ApiResponse<>(1000, "Tải bảng vẽ thành công", content);
    }

    // --- 2. XỬ LÝ VĂN BẢN (EDITOR / DOCS) ---

    // Nhận chữ từ Socket và phát lại cho nhóm
    @MessageMapping("/editor/{teamId}")
    public void syncEditor(@DestinationVariable("teamId") String teamId, @Payload String data) {
        messagingTemplate.convertAndSend("/topic/collab/editor/" + teamId, data);
        
        // Lưu trữ vào bảng riêng của Editor
        EditorData ed = editorRepo.findById(teamId).orElse(new EditorData());
        ed.setTeamId(teamId);
        ed.setContent(data);
        ed.setUpdatedAt(LocalDateTime.now());
        editorRepo.save(ed);
    }

    // API lấy nội dung văn bản cũ khi vào phòng
    @GetMapping("/editor/{teamId}")
    public ApiResponse<String> getEditor(@PathVariable("teamId") String teamId) {
        String content = editorRepo.findById(teamId)
                .map(EditorData::getContent)
                .orElse("");
        return new ApiResponse<>(1000, "Tải văn bản thành công", content);
    }
}