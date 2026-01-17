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

    // 2. Lấy danh sách (Hỗ trợ hàm getStudentsByClass bị thiếu)
    List<ClassEnrollment> findByClassId(Long classId);

    // 3. 👇 HÀM QUAN TRỌNG ĐỂ XÓA (Tìm bản ghi để Service gọi delete())
    Optional<ClassEnrollment> findByClassIdAndStudentId(Long classId, String studentId);
}