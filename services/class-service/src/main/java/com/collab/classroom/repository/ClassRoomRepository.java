package com.collab.classroom.repository;

import com.collab.classroom.entity.ClassEnrollment;
import com.collab.classroom.entity.ClassRoom; // Import đúng ClassRoom
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    
    boolean existsByClassCode(String classCode);
    
    Optional<ClassRoom> findByClassCode(String classCode);
    List<ClassRoom> findByTeacherId(String teacherId);

}