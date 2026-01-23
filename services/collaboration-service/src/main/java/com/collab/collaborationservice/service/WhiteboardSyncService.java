package com.collab.collaborationservice.service;

import com.collab.collaborationservice.entity.WhiteboardData;
import com.collab.collaborationservice.repository.WhiteboardRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WhiteboardSyncService {

    @Autowired
    private WhiteboardRepository repo;

    @RabbitListener(queues = "whiteboard_save_queue")
    @Transactional 
    public void saveToDatabase(Map<String, Object> payload) {
        try {
            String roomId = (String) payload.get("roomId");
            String newData = (String) payload.get("data");

            if (roomId == null || newData == null) return;

            // 1. Dùng findById thay vì findByRoomId
            WhiteboardData data = repo.findById(roomId).orElse(new WhiteboardData());
            
            // 2. Sửa setRoomId thành setTeamId
            data.setTeamId(roomId);
            data.setContent(newData);
            
            // 3. Sửa setLastUpdated thành setUpdatedAt
            data.setUpdatedAt(LocalDateTime.now());
            
            repo.save(data);
            System.out.println("✅ Đã lưu dữ liệu cộng tác cho: " + roomId);
        } catch (Exception e) {
            System.err.println("Lỗi lưu RabbitMQ: " + e.getMessage());
        }
    }
}