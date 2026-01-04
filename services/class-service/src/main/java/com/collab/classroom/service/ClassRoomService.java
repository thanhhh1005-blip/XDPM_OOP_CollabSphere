package com.collab.classroom.service;

import com.collab.classroom.client.SubjectClient;
import com.collab.classroom.entity.ClassEnrollment; // <--- Import Entity mới
import com.collab.classroom.entity.ClassRoom;
import com.collab.classroom.repository.ClassEnrollmentRepository; // <--- Import Repo mới
import com.collab.classroom.repository.ClassRoomRepository;
import com.collab.shared.dto.ClassroomDTO;
import com.collab.shared.dto.SubjectDTO;
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
@Slf4j
public class ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final SubjectClient subjectClient;
    
    // 1. CẬP NHẬT: Inject thêm Repository quản lý sinh viên
    private final ClassEnrollmentRepository classEnrollmentRepository; 

    // --- 1. TẠO LỚP HỌC MỚI ---
    public ClassroomDTO createClass(ClassroomDTO dto) {
        if (classRoomRepository.existsByClassCode(dto.getCode())) {
            throw new RuntimeException("Mã lớp " + dto.getCode() + " đã tồn tại!");
        }

        // Logic check Subject giữ nguyên
        SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
        if (subject == null) {
            throw new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId());
        }

        ClassRoom classRoom = mapToEntity(dto);
        // teacherId đã được map trong hàm mapToEntity bên dưới
        return mapToDTO(classRoomRepository.save(classRoom));
    }

    // --- 2. LẤY CHI TIẾT ---
    public ClassroomDTO getClassById(Long id) {
        ClassRoom classRoom = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp ID: " + id));

        ClassroomDTO dto = mapToDTO(classRoom);
        try {
            SubjectDTO subject = subjectClient.getSubjectById(classRoom.getSubjectId());
            dto.setSubject(subject);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin môn học: " + e.getMessage());
        }
        return dto;
    }

    // --- 3. LẤY DANH SÁCH ---
    public List<ClassroomDTO> getAllClasses() {
        return classRoomRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- 4. IMPORT EXCEL (CẬP NHẬT ĐỂ ĐỌC TEACHER ID) ---
    @Transactional
    public void importClasses(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<ClassRoom> classesToSave = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String classCode = getCellValue(row.getCell(0));
                String subjectCode = getCellValue(row.getCell(1));
                String semester = getCellValue(row.getCell(2));
                String room = getCellValue(row.getCell(3));
                
                // CẬP NHẬT: Đọc thêm cột thứ 4 là TeacherID (Mã giảng viên)
                String teacherId = getCellValue(row.getCell(4)); 

                if (classCode.isEmpty() || subjectCode.isEmpty()) continue;
                if (classRoomRepository.existsByClassCode(classCode)) continue;

                try {
                    SubjectDTO subject = subjectClient.getSubjectByCode(subjectCode);
                    if (subject != null) {
                        ClassRoom classRoom = ClassRoom.builder()
                                .classCode(classCode)
                                .subjectId(subject.getId())
                                .semester(semester)
                                .room(room)
                                .teacherId(teacherId) // <--- CẬP NHẬT: Lưu teacherId vào DB
                                .isActive(true)
                                .build();
                        classesToSave.add(classRoom);
                    }
                } catch (Exception e) {
                    log.error("Bỏ qua môn học lỗi hoặc không tìm thấy môn: " + subjectCode);
                }
            }

            if (!classesToSave.isEmpty()) {
                classRoomRepository.saveAll(classesToSave);
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi import file: " + e.getMessage());
        }
    }

    // --- 5. CẬP NHẬT MỚI: HÀM THÊM SINH VIÊN VÀO LỚP ---
    public void addStudentToClass(Long classId, String studentId) {
        // Kiểm tra lớp có tồn tại không
        if (!classRoomRepository.existsById(classId)) {
            throw new RuntimeException("Lớp học không tồn tại!");
        }

        // Kiểm tra xem sinh viên đã có trong lớp chưa để tránh trùng lặp
        if (classEnrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new RuntimeException("Sinh viên " + studentId + " đã có trong lớp này rồi!");
        }
        // Lưu sinh viên vào lớp
        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setClassId(classId);
        enrollment.setStudentId(studentId);
        
        classEnrollmentRepository.save(enrollment);
    }

    // --- HELPERS ---
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf(cell.getNumericCellValue());
    }

    private ClassroomDTO mapToDTO(ClassRoom entity) {
        return ClassroomDTO.builder()
                .id(entity.getId())
                .code(entity.getClassCode())
                .subjectId(entity.getSubjectId())
                .semester(entity.getSemester())
                .room(entity.getRoom())
                .teacherId(entity.getTeacherId()) // <--- CẬP NHẬT: Map teacherId ra DTO
                .build();
    }

    private ClassRoom mapToEntity(ClassroomDTO dto) {
        return ClassRoom.builder()
                .classCode(dto.getCode())
                .subjectId(dto.getSubjectId())
                .semester(dto.getSemester())
                .room(dto.getRoom())
                .teacherId(dto.getTeacherId()) // <--- CẬP NHẬT: Map teacherId vào Entity
                .isActive(true)
                .build();
    }
}