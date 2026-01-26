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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
    // 👇 1. INJECT NIFI CLIENT (MỚI THÊM)
    private final NifiClient nifiClient;

// =========================================================================
    // 1. TẠO LỚP HỌC MỚI (Đã sửa để Import êm ru)
    // =========================================================================
    public ClassroomDTO createClass(ClassroomDTO dto) {

        // 🛡️ 1. CHẶN DÒNG TIÊU ĐỀ (HEADER) CỦA EXCEL
        // Nếu mã lớp có chữ "Code" hoặc Mã môn có chữ "ID" thì bỏ qua luôn
        if ((dto.getCode() != null && dto.getCode().toLowerCase().contains("code")) ||
            (dto.getSubjectCode() != null && dto.getSubjectCode().toUpperCase().contains("ID"))) {
            log.warn("Bỏ qua dòng tiêu đề Excel.");
            return null; // Trả về null -> Controller trả về 200 OK -> NiFi đi tiếp
        }

        // 🛡️ 2. XỬ LÝ TRÙNG LẶP (QUAN TRỌNG)
        // Nếu lớp đã tồn tại -> Chỉ Log cảnh báo và Return null (KHÔNG NÉM LỖI NỮA)
        if (classRoomRepository.existsByClassCode(dto.getCode())) {
            log.warn("Mã lớp {} đã tồn tại -> Bỏ qua import dòng này.", dto.getCode());
            return null; // <--- Chìa khóa để NiFi không báo đỏ là đây!
        }

        try {
            // 👇 LOGIC MỚI: Xử lý trường hợp Import từ NiFi (chỉ có Code, chưa có ID)
            if (dto.getSubjectId() == null && dto.getSubjectCode() != null) {
                // Gọi Subject Service tìm ID dựa trên Code
                SubjectDTO subject = subjectClient.getSubjectByCode(dto.getSubjectCode());
                if (subject != null) {
                    dto.setSubjectId(subject.getId()); // Gán ID tìm được vào DTO
                } else {
                    // Nếu không tìm thấy môn thì cũng chỉ Log và bỏ qua (để không chết cả dây chuyền)
                    log.error("Không tìm thấy môn học mã: {} -> Bỏ qua lớp {}", dto.getSubjectCode(), dto.getCode());
                    return null; 
                }
            }
            
            // 👇 LOGIC CŨ: Check lại ID (để đảm bảo an toàn cho Frontend gọi)
            if (dto.getSubjectId() != null) {
                SubjectDTO subject = subjectClient.getSubjectById(dto.getSubjectId());
                if (subject == null) {
                    throw new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId());
                }
            }

        } catch (Exception e) {
            // Bắt lỗi kết nối nhưng không ném 500 ra ngoài khi đang import
            log.error("Lỗi xử lý môn học: " + e.getMessage());
            return null; // Bỏ qua dòng lỗi này
        }

        // Lưu vào DB
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
    
    public List<ClassEnrollment> getStudentsByClass(Long classId) {
        return classEnrollmentRepository.findByClassId(classId);
    }

    // =========================================================================
    // 3. IMPORT EXCEL (ĐÃ SỬA ĐỂ DÙNG NIFI) 🚀
    // =========================================================================
    public void importClasses(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File excel rỗng!");
        
        // Gọi sang NiFi Client, bắn vào endpoint "classes"
        nifiClient.sendFile(file, "classes");
        
        log.info("Đã chuyển file Excel sang NiFi xử lý thành công!");
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

    public void removeStudentFromClass(Long classId, String studentId) {
        ClassEnrollment enrollment = classEnrollmentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên " + studentId + " không có trong lớp này!"));

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
        List<ClassEnrollment> enrollments = classEnrollmentRepository.findByStudentId(studentId);
        
        List<Long> classIds = enrollments.stream()
                .map(ClassEnrollment::getClassId)
                .collect(Collectors.toList());

        List<ClassRoom> entities = classRoomRepository.findAllById(classIds);
        List<ClassroomDTO> dtos = entities.stream().map(this::mapToDTO).collect(Collectors.toList());
        
        dtos.forEach(this::enrichClassroomDTO); 
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

    @Transactional 
    public void addStudentsToClass(Long classId, List<String> studentIds) {
        if (!classRoomRepository.existsById(classId)) {
            throw new RuntimeException("Lớp học không tồn tại!");
        }

        List<ClassEnrollment> newEnrollments = new ArrayList<>();

        for (String studentId : studentIds) {
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