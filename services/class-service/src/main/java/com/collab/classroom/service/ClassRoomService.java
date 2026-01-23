package com.collab.classroom.service;

import com.collab.classroom.client.IdentityClient;
import com.collab.classroom.client.SubjectClient;
import com.collab.classroom.entity.ClassEnrollment;
import com.collab.classroom.entity.ClassRoom;
import com.collab.classroom.repository.ClassEnrollmentRepository;
import com.collab.classroom.repository.ClassRoomRepository;
import com.collab.shared.dto.ClassroomDTO;
import com.collab.shared.dto.SubjectDTO;
import com.collab.shared.dto.UserDTO;
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
    private final ClassEnrollmentRepository classEnrollmentRepository;
    
    // --- CLIENTS ---
    private final SubjectClient subjectClient;   
    private final IdentityClient identityClient; 

    // =========================================================================
    // 1. TẠO LỚP HỌC MỚI
    // =========================================================================
    public ClassroomDTO createClass(ClassroomDTO dto) {
        if (classRoomRepository.existsByClassCode(dto.getCode())) {
            throw new RuntimeException("Mã lớp " + dto.getCode() + " đã tồn tại!");
        }

        try {
            SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
            if (subject == null) {
                throw new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId());
            }
        } catch (Exception e) {
            log.error("Lỗi kết nối Subject Service: " + e.getMessage());
            throw new RuntimeException("Lỗi xác thực môn học: " + e.getMessage());
        }

        ClassRoom classRoom = mapToEntity(dto);
        ClassRoom savedClass = classRoomRepository.save(classRoom);
        
        ClassroomDTO resultDTO = mapToDTO(savedClass);
        enrichClassroomDTO(resultDTO); 
        return resultDTO;
    }

    // =========================================================================
    // 2. LẤY CHI TIẾT & DANH SÁCH
    // =========================================================================
    public ClassroomDTO getClassById(Long id) {
        ClassRoom classRoom = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp ID: " + id));

        ClassroomDTO dto = mapToDTO(classRoom);
        enrichClassroomDTO(dto);
        return dto;
    }

    public List<ClassroomDTO> getAllClasses() {
        List<ClassRoom> entities = classRoomRepository.findAll();
        
        List<ClassroomDTO> dtos = entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        dtos.forEach(this::enrichClassroomDTO);

        return dtos;
    }

    public List<ClassroomDTO> getClassesByTeacher(String teacherId) {
        List<ClassRoom> entities = classRoomRepository.findByTeacherId(teacherId);
        
        List<ClassroomDTO> dtos = entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // Điền thêm thông tin Môn học (cho đẹp)
        dtos.forEach(this::enrichClassroomDTO);
        return dtos;
    }
    
    // 👇 ĐÂY LÀ HÀM BẠN BỊ THIẾU (Gây lỗi undefined ở Controller) 👇
    public List<ClassEnrollment> getStudentsByClass(Long classId) {
        return classEnrollmentRepository.findByClassId(classId);
    }

    // =========================================================================
    // 3. IMPORT EXCEL
    // =========================================================================
    @Transactional
    public void importClasses(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File excel rỗng!");

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<ClassRoom> classesToSave = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter(); 

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 

                String classCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectCode = dataFormatter.formatCellValue(row.getCell(1)).trim(); 
                String teacherUsername = dataFormatter.formatCellValue(row.getCell(2)).trim(); 
                String room = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String semester = dataFormatter.formatCellValue(row.getCell(4)).trim();

                if (classCode.isEmpty() || subjectCode.isEmpty() || teacherUsername.isEmpty()) continue;
                if (classRoomRepository.existsByClassCode(classCode)) continue;

                try {
                    SubjectDTO subject = subjectClient.getSubjectByCode(subjectCode);
                    
                    if (subject != null) {
                        ClassRoom classRoom = ClassRoom.builder()
                                .classCode(classCode)
                                .subjectId(subject.getId())
                                .teacherId(teacherUsername)
                                .semester(semester)
                                .room(room)
                                .isActive(true)
                                .build();
                        classesToSave.add(classRoom);
                    } else {
                        log.warn("Import bỏ qua: Không tìm thấy môn học mã " + subjectCode);
                    }
                } catch (Exception e) {
                    log.error("Lỗi dòng {}: {}", row.getRowNum(), e.getMessage());
                }
            }

            if (!classesToSave.isEmpty()) {
                classRoomRepository.saveAll(classesToSave);
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. QUẢN LÝ SINH VIÊN (ADD & REMOVE)
    // =========================================================================
    public void addStudentToClass(Long classId, String studentId) {
        if (!classRoomRepository.existsById(classId)) {
            throw new RuntimeException("Lớp học không tồn tại!");
        }

        if (classEnrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new RuntimeException("Sinh viên " + studentId + " đã có trong lớp này rồi!");
        }

        try {
            UserDTO student = identityClient.getUserByUsername(studentId);
            if (student == null) {
                 throw new RuntimeException("Mã sinh viên không tồn tại trên hệ thống!");
            }
        } catch (Exception e) {
            log.warn("Không thể xác thực sinh viên bên Identity Service: " + e.getMessage());
        }
        
        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setClassId(classId);
        enrollment.setStudentId(studentId);
        
        classEnrollmentRepository.save(enrollment);
    }

    // 👇 HÀM XÓA "BẤT TỬ" (KHÔNG SỬA, CHỈ BẢO ĐẢM Repository CÓ HÀM findByClassIdAndStudentId)
    public void removeStudentFromClass(Long classId, String studentId) {
        // 1. Tìm bản ghi
        ClassEnrollment enrollment = classEnrollmentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên " + studentId + " không có trong lớp này!"));

        // 2. Xóa bản ghi (Dùng hàm delete chuẩn của JPA -> Tránh lỗi Transaction 500)
        classEnrollmentRepository.delete(enrollment);
    }

    // =========================================================================
    // 5. CẬP NHẬT & XÓA LỚP
    // =========================================================================
    public ClassroomDTO updateClass(Long id, ClassroomDTO dto) {
        ClassRoom existingClass = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học ID: " + id));

        if (dto.getSubjectId() != null && !dto.getSubjectId().equals(existingClass.getSubjectId())) {
             try {
                 SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
                 if (subject == null) throw new RuntimeException("Môn học không tồn tại");
                 existingClass.setSubjectId(dto.getSubjectId());
             } catch (Exception e) {
                 throw new RuntimeException("Lỗi check môn học: " + e.getMessage());
             }
        }

        if (dto.getRoom() != null) existingClass.setRoom(dto.getRoom());
        if (dto.getSemester() != null) existingClass.setSemester(dto.getSemester());
        if (dto.getTeacherId() != null) existingClass.setTeacherId(dto.getTeacherId());

        ClassRoom saved = classRoomRepository.save(existingClass);
        ClassroomDTO result = mapToDTO(saved);
        enrichClassroomDTO(result); 
        return result;
    }

    public void deleteClass(Long id) {
        if (!classRoomRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lớp học để xóa!");
        }
        classRoomRepository.deleteById(id);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================
    private void enrichClassroomDTO(ClassroomDTO dto) {
        if (dto.getSubjectId() != null) {
            try {
                SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
                dto.setSubject(subject);
            } catch (Exception e) {
                log.error("Lỗi lấy Subject ID {}: {}", dto.getSubjectId(), e.getMessage());
            }
        }

        if (dto.getTeacherId() != null && !dto.getTeacherId().isEmpty()) {
            try {
                UserDTO teacher = identityClient.getUserByUsername(dto.getTeacherId());
                dto.setTeacher(teacher);
            } catch (Exception e) {
                log.error("Lỗi lấy Teacher {}: {}", dto.getTeacherId(), e.getMessage());
            }
        }
    }

    public List<ClassroomDTO> getClassesForStudent(String studentId) {
    // 1. Tìm tất cả bản ghi ghi danh của sinh viên này
    List<ClassEnrollment> enrollments = classEnrollmentRepository.findByStudentId(studentId);
    
    // 2. Lấy danh sách ID lớp từ các bản ghi ghi danh đó
    List<Long> classIds = enrollments.stream()
            .map(ClassEnrollment::getClassId)
            .collect(Collectors.toList());

    // 3. Tìm các lớp tương ứng và đổi sang DTO
    List<ClassRoom> entities = classRoomRepository.findAllById(classIds);
    List<ClassroomDTO> dtos = entities.stream().map(this::mapToDTO).collect(Collectors.toList());
    
    dtos.forEach(this::enrichClassroomDTO); // Điền thêm tên môn, tên GV cho đẹp
    return dtos;
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

        // =========================================================================
    // THÊM NHIỀU SINH VIÊN CÙNG LÚC
    // =========================================================================
    @Transactional // Quan trọng: Đảm bảo nếu lỗi 1 người thì sẽ không lưu tất cả
    public void addStudentsToClass(Long classId, List<String> studentIds) {
        if (!classRoomRepository.existsById(classId)) {
            throw new RuntimeException("Lớp học không tồn tại!");
        }

        List<ClassEnrollment> newEnrollments = new ArrayList<>();

        for (String studentId : studentIds) {
            // Nếu sinh viên chưa có trong lớp thì mới thêm
            if (!classEnrollmentRepository.existsByClassIdAndStudentId(classId, studentId)) {
                ClassEnrollment enrollment = new ClassEnrollment();
                enrollment.setClassId(classId);
                enrollment.setStudentId(studentId);
                newEnrollments.add(enrollment);
            }
        }

        if (!newEnrollments.isEmpty()) {
            classEnrollmentRepository.saveAll(newEnrollments);
            log.info("Đã thêm {} sinh viên vào lớp {}", newEnrollments.size(), classId);
        }
    }
}