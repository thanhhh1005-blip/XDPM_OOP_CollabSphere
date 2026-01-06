package com.collab.communicationservice.repository;

import com.collab.communicationservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Tạm thời để trống, JpaRepository đã có sẵn hàm save(), findAll() rồi
    // Nếu cần, em có thể thêm: List<ChatMessage> findByRoomId(Long roomId);
}