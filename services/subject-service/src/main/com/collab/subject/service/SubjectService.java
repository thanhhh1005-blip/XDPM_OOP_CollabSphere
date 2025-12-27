package com.collab.subject.service;

import com.collab.subject.entity.Subject;
import com.collab.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository repository;

    public Subject createSubject(Subject subject) {
        // Kiểm tra xem mã môn đã tồn tại chưa
        if (repository.findByCode(subject.getCode()).isPresent()) {
            throw new RuntimeException("Môn học với mã " + subject.getCode() + " đã tồn tại!");
        }
        return repository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return repository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + id));
    }
}