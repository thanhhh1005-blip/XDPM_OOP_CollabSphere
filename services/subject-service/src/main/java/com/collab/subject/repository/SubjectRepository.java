package com.collab.subject.repository;

import com.collab.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Tìm môn học theo mã code (VD: để check trùng khi import)
    Optional<Subject> findByCode(String code);
    
    // Kiểm tra mã môn đã tồn tại chưa
    boolean existsByCode(String code);
}