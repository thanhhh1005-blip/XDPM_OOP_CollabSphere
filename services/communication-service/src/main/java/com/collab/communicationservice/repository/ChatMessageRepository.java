package com.collab.communicationservice.repository;

import com.collab.communicationservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 👇 SỬA DÒNG NÀY: Tham số roomId truyền vào là String
    List<ChatMessage> findByRoomId(String roomId);
}