package com.collab.classroom.service;

import com.collab.classroom.entity.ClassRoom;
import com.collab.classroom.repository.ClassRepository;
import com.collab.shared.dto.SubjectDTO; // Import từ Shared Module
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository repository;
    private final RestTemplate restTemplate;

    public ClassRoom createClass(ClassRoom classRoom) {
        // 1. Kiểm tra mã lớp trùng
        if (repository.existsByCode(classRoom.getCode())) {
            throw new RuntimeException("Mã lớp " + classRoom.getCode() + " đã tồn tại!");
        }

        // 2. GỌI SANG SUBJECT-SERVICE (Port 8081)
        String url = "http://127.0.0.1:8081/..." + classRoom.getSubjectId();

        try {
            // Nếu tìm thấy môn học, API trả về SubjectDTO
            SubjectDTO subject = restTemplate.getForObject(url, SubjectDTO.class);
            System.out.println("Tạo lớp cho môn: " + subject.getName());
        } catch (Exception e) {
            // Nếu subject-service trả về lỗi (404) hoặc không kết nối được
            throw new RuntimeException("Môn học ID " + classRoom.getSubjectId() + " không tồn tại!");
        }

        // 3. Lưu lớp học
        classRoom.setStatus("PLANNED");
        return repository.save(classRoom);
    }

    public List<ClassRoom> getAllClasses() {
        return repository.findAll();
    }
}