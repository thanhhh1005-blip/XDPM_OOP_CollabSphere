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

    // --- 1. TẠO MÔN HỌC (Có chuyển đổi DTO) ---
    public SubjectDTO createSubject(SubjectDTO dto) {
        // Kiểm tra trùng mã
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
    
    // --- 4. LẤY CHI TIẾT THEO MÃ (Dùng cho Class-Service gọi sang) ---
    public SubjectDTO getSubjectByCode(String code) {
        Subject subject = repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với mã: " + code));
        return mapToDTO(subject);
    }

    // --- 5. TÍNH NĂNG IMPORT EXCEL (Quan trọng) ---
    @Transactional
    public void importSubjects(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File không được rỗng");

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Subject> subjectsToSave = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                // Đọc các cột: 0:Mã, 1:Tên, 2:Tín chỉ, 3:Mô tả (nếu có)
                String code = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));
                String creditsStr = getCellValue(row.getCell(2));

                if (code == null || code.trim().isEmpty()) continue;
                if (repository.existsByCode(code)) continue; // Bỏ qua nếu đã có

                int credits = 0;
                try {
                    credits = (int) Double.parseDouble(creditsStr);
                } catch (Exception e) { credits = 0; }

                Subject subject = Subject.builder()
                        .code(code)
                        .name(name)
                        .credits(credits)
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

    // --- HELPER METHODS (Chuyển đổi dữ liệu) ---
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
                .isActive(s.getIsActive())
                .build();
    }

    private Subject mapToEntity(SubjectDTO d) {
        return Subject.builder()
                .code(d.getCode())
                .name(d.getName())
                .credits(d.getCredits())
                .isActive(d.getIsActive() != null ? d.getIsActive() : true)
                .build();
    }
}