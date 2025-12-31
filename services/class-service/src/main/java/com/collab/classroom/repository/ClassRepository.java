package com.collab.classroom.repository;

import com.collab.classroom.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<ClassRoom, Long> {
    boolean existsByCode(String code);
}