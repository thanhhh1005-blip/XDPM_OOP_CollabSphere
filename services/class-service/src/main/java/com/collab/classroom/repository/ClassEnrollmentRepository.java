package com.collab.classroom.repository;

import com.collab.classroom.entity.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {
    
    // Hàm tìm tất cả sinh viên thuộc một lớp
    List<ClassEnrollment> findByClassId(Long classId);
    
    // --- THÊM DÒNG NÀY ĐỂ FIX LỖI ---
    // Kiểm tra sinh viên có trong lớp chưa (trả về true/false)
    boolean existsByClassIdAndStudentId(Long classId, String studentId);
}