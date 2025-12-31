package com.collab.subject.repository;

import com.collab.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Tìm môn học theo mã môn (để check trùng)
    Optional<Subject> findByCode(String code);
}