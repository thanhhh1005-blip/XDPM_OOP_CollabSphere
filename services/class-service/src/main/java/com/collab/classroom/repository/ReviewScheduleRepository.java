package com.collab.classroom.repository;

import com.collab.classroom.entity.ReviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewScheduleRepository extends JpaRepository<ReviewSchedule, Long> {
    
    List<ReviewSchedule> findByClassId(Long classId);
}