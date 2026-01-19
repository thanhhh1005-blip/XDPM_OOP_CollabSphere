package com.collab.communicationservice.repository;

import com.collab.communicationservice.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    // JpaRepository đã có sẵn findById, save, deleteById... 
    // nên chúng ta không cần viết thêm gì ở đây cả.
}