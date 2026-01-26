package com.collab.subject.service;

import com.collab.shared.dto.SubjectDTO;
import com.collab.subject.entity.Subject;
import com.collab.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j 
public class SubjectService {

    private final SubjectRepository repository;
    
    // 👇 1. INJECT NIFI CLIENT
    private final NifiClient nifiClient;

    // --- 1. TẠO MÔN HỌC ---
    public SubjectDTO createSubject(SubjectDTO dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Môn học với mã " + dto.getCode() + " đã tồn tại!");
        }
        
        Subject subject = mapToEntity(dto);
        Subject savedSubject = repository.save(subject);
        return mapToDTO(savedSubject);
    }

    // --- 2. LẤY TẤT CẢ ---
    public List<SubjectDTO> getAllSubjects() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- 3. LẤY CHI TIẾT THEO ID ---
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + id));
        return mapToDTO(subject);
    }
    
    // --- 4. LẤY CHI TIẾT THEO MÃ ---
    public SubjectDTO getSubjectByCode(String code) {
        Subject subject = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với mã: " + code));
        return mapToDTO(subject);
    }

    // --- 5. CẬP NHẬT MÔN HỌC ---
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        Subject existingSubject = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học ID: " + id));

        existingSubject.setName(dto.getName());
        
        if (dto.getCredits() != null) { 
             existingSubject.setCredits(dto.getCredits());
        }

        if (dto.getDescription() != null) {
            existingSubject.setDescription(dto.getDescription());
        }
        
        if (dto.getIsActive() != null) {
            existingSubject.setIsActive(dto.getIsActive());
        }

        return mapToDTO(repository.save(existingSubject));
    }

    // --- 6. XÓA MÔN HỌC ---
    public void deleteSubject(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Môn học không tồn tại!");
        }
        repository.deleteById(id);
    }

    // --- 7. IMPORT EXCEL (ĐÃ RÚT GỌN VỚI NIFI) 🚀 ---
    public void importSubjects(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File không được rỗng");

        // Gọi sang NiFi Client, bắn vào endpoint "subjects"
        nifiClient.sendFile(file, "subjects");
        
        log.info("Đã chuyển file Subject sang NiFi xử lý thành công!");
    }

    // --- HELPER METHODS ---
    // (Đã xóa hàm getCellValue vì không còn dùng nữa)

    private SubjectDTO mapToDTO(Subject s) {
        return SubjectDTO.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .credits(s.getCredits())
                .description(s.getDescription())
                .isActive(s.getIsActive())
                .build();
    }

    private Subject mapToEntity(SubjectDTO d) {
        return Subject.builder()
                .code(d.getCode())
                .name(d.getName())
                .credits(d.getCredits())
                .description(d.getDescription())
                .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                .build();
    }
}