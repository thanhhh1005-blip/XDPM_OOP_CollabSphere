package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.WhiteboardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WhiteboardRepository extends JpaRepository<WhiteboardData, String> {
    // Không cần viết hàm findByRoomId vì teamId đã là @Id rồi
}