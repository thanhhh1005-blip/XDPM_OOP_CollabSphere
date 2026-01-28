package com.collab.classroom.repository;

import com.collab.classroom.entity.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // 👈 Quan trọng: Phải có import này

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {

    // 1. Kiểm tra tồn tại
    boolean existsByClassIdAndStudentId(Long classId, String studentId);

    // 2. Lấy danh sách 
    List<ClassEnrollment> findByClassId(Long classId);

    Optional<ClassEnrollment> findByClassIdAndStudentId(Long classId, String studentId);

    List<ClassEnrollment> findByStudentId(String studentId);
}