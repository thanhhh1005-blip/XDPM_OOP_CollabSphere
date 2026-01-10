package com.collab.subject.service;

import com.collab.shared.dto.SubjectDTO;
import com.collab.subject.entity.Subject;
import com.collab.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Giúp ghi log lỗi ra màn hình console
public class SubjectService {

    private final SubjectRepository repository;

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
        // 1. Tìm môn học cũ
        Subject existingSubject = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học ID: " + id));

        // 2. Cập nhật thông tin
        existingSubject.setName(dto.getName());
        
        // Kiểm tra null để tránh lỗi
        if (dto.getCredits() != null) { 
             existingSubject.setCredits(dto.getCredits());
        }

        // --- CẬP NHẬT MÔ TẢ (NẾU CÓ) ---
        if (dto.getDescription() != null) {
            existingSubject.setDescription(dto.getDescription());
        }
        
        if (dto.getIsActive() != null) {
            existingSubject.setIsActive(dto.getIsActive());
        }

        // 3. Lưu lại
        return mapToDTO(repository.save(existingSubject));
    }

    // --- 6. XÓA MÔN HỌC ---
    public void deleteSubject(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Môn học không tồn tại!");
        }
        repository.deleteById(id);
    }

    // --- 7. IMPORT EXCEL (ĐÃ CẬP NHẬT ĐỂ ĐỌC MÔ TẢ) ---
    @Transactional
    public void importSubjects(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File không được rỗng");

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Subject> subjectsToSave = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                String code = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));
                String creditsStr = getCellValue(row.getCell(2));
                
                // --- ĐỌC CỘT THỨ 4: MÔ TẢ / ĐỀ CƯƠNG ---
                String description = getCellValue(row.getCell(3));

                if (code == null || code.trim().isEmpty()) continue;
                if (repository.existsByCode(code)) continue;

                int credits = 0;
                try {
                    credits = (int) Double.parseDouble(creditsStr);
                } catch (Exception e) { credits = 0; }

                Subject subject = Subject.builder()
                        .code(code)
                        .name(name)
                        .credits(credits)
                        .description(description) // --- LƯU VÀO DATABASE ---
                        .isActive(true)
                        .build();

                subjectsToSave.add(subject);
            }

            if (!subjectsToSave.isEmpty()) {
                repository.saveAll(subjectsToSave);
                log.info("Đã import thành công {} môn học", subjectsToSave.size());
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    // --- HELPER METHODS ---
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            default: return "";
        }
    }

    private SubjectDTO mapToDTO(Subject s) {
        return SubjectDTO.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .credits(s.getCredits())
                .description(s.getDescription()) // --- MAP RA DTO ---
                .isActive(s.getIsActive())
                .build();
    }

    private Subject mapToEntity(SubjectDTO d) {
        return Subject.builder()
                .code(d.getCode())
                .name(d.getName())
                .credits(d.getCredits())
                .description(d.getDescription()) // --- MAP VÀO ENTITY ---
                .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                .build();
    }
}