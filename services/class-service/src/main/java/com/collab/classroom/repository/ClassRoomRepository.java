package com.collab.classroom.repository;

import com.collab.classroom.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    
    boolean existsByClassCode(String classCode);
    
    Optional<ClassRoom> findByClassCode(String classCode);

    @Query("SELECT c FROM ClassRoom c WHERE LOWER(c.teacherId) = LOWER(:teacherId) AND c.isActive = true")
    List<ClassRoom> findByTeacherId(@Param("teacherId") String teacherId);
}