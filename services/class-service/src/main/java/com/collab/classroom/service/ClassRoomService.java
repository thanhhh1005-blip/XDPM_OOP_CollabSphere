package com.collab.classroom.service;

import com.collab.classroom.client.SubjectClient;
import com.collab.classroom.entity.ClassEnrollment;
import com.collab.classroom.entity.ClassRoom;
import com.collab.classroom.repository.ClassEnrollmentRepository;
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
    private final ClassEnrollmentRepository classEnrollmentRepository;

    // --- 1. TẠO LỚP HỌC MỚI ---
    public ClassroomDTO createClass(ClassroomDTO dto) {
        if (classRoomRepository.existsByClassCode(dto.getCode())) {
            throw new RuntimeException("Mã lớp " + dto.getCode() + " đã tồn tại!");
        }

        SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
        if (subject == null) {
            throw new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId());
        }

        ClassRoom classRoom = mapToEntity(dto);
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

    // --- 4. IMPORT EXCEL ---
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
                                .teacherId(teacherId)
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

    // --- 5. THÊM SINH VIÊN VÀO LỚP ---
    public void addStudentToClass(Long classId, String studentId) {
        if (!classRoomRepository.existsById(classId)) {
            throw new RuntimeException("Lớp học không tồn tại!");
        }

        if (classEnrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new RuntimeException("Sinh viên " + studentId + " đã có trong lớp này rồi!");
        }
        
        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setClassId(classId);
        enrollment.setStudentId(studentId);
        
        classEnrollmentRepository.save(enrollment);
    }

    public List<ClassEnrollment> getStudentsByClass(Long classId) {
        return classEnrollmentRepository.findByClassId(classId);
    }

    // =========================================================
    // PHẦN BỔ SUNG MỚI (UPDATE & DELETE)
    // =========================================================

    // --- 6. CẬP NHẬT LỚP HỌC (MỚI) ---
    public ClassroomDTO updateClass(Long id, ClassroomDTO dto) {
        // 1. Tìm lớp học cũ
        ClassRoom existingClass = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học ID: " + id));

        // 2. Kiểm tra môn học (nếu có thay đổi subjectId)
        if (dto.getSubjectId() != null && !dto.getSubjectId().equals(existingClass.getSubjectId())) {
             SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
             if (subject == null) {
                 throw new RuntimeException("Môn học mới không tồn tại!");
             }
             existingClass.setSubjectId(dto.getSubjectId());
        }

        // 3. Cập nhật các thông tin khác
        // Lưu ý: Thường không cho sửa Mã Lớp (ClassCode) tùy nghiệp vụ, ở đây mình cho phép sửa Room, Semester, Teacher
        if (dto.getRoom() != null) existingClass.setRoom(dto.getRoom());
        if (dto.getSemester() != null) existingClass.setSemester(dto.getSemester());
        if (dto.getTeacherId() != null) existingClass.setTeacherId(dto.getTeacherId());
        // Nếu muốn cho sửa classCode thì mở comment dòng dưới:
        // if (dto.getCode() != null) existingClass.setClassCode(dto.getCode());

        // 4. Lưu lại
        return mapToDTO(classRoomRepository.save(existingClass));
    }

    // --- 7. XÓA LỚP HỌC (MỚI) ---
    public void deleteClass(Long id) {
        if (!classRoomRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lớp học để xóa!");
        }
        // Lưu ý: Nếu lớp đã có sinh viên (bảng ClassEnrollment), việc xóa cứng (deleteById) có thể gây lỗi khóa ngoại
        // Bạn có thể cần xóa Enrollments trước hoặc dùng Soft Delete (isActive = false).
        // Hiện tại mình để xóa cứng theo yêu cầu cơ bản:
        classRoomRepository.deleteById(id);
    }

    // =========================================================

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
                .teacherId(entity.getTeacherId())
                .build();
    }

    private ClassRoom mapToEntity(ClassroomDTO dto) {
        return ClassRoom.builder()
                .classCode(dto.getCode())
                .subjectId(dto.getSubjectId())
                .semester(dto.getSemester())
                .room(dto.getRoom())
                .teacherId(dto.getTeacherId())
                .isActive(true)
                .build();
    }
}