package com.collab.projectservice.service;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.domain.ProjectStatus;
import com.collab.projectservice.domain.ProjectSeq;
import com.collab.projectservice.domain.Syllabus; // ✅ Import Syllabus
import com.collab.projectservice.repo.ProjectRepository;
import com.collab.projectservice.repo.ProjectSeqRepository;
import com.collab.projectservice.infra.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectAppService {

    private final ProjectRepository repo;
    private final EventPublisher eventPublisher;
    private final ProjectSeqRepository seqRepo;

    /**
     * Tạo project mới kèm theo nội dung đề cương (Syllabus)
     */
    @Transactional
    public Project create(String title, String description, String syllabusContent) {
        // 1. Sinh mã dự án (VD: PR0001)
        String projectCode = generateProjectCode();

        // 2. Tạo đối tượng Project
        Project p = new Project();
        p.setProjectCode(projectCode);
        p.setTitle(title); // Lưu ý: Trong Entity bạn đặt là 'name' hay 'title' thì sửa lại cho khớp
        p.setDescription(description);
        p.setStatus(ProjectStatus.DRAFT);

        // 3. Xử lý Syllabus (Nếu có nội dung)
        if (syllabusContent != null && !syllabusContent.isEmpty()) {
            Syllabus syllabus = new Syllabus();
            syllabus.setContent(syllabusContent);
            
            // Gán 2 chiều (nếu Entity có cấu hình)
            // syllabus.setProject(p); 
            
            // Gán Syllabus vào Project
            p.setSyllabus(syllabus);
        }

        // 4. Lưu (Cascade.ALL sẽ tự lưu Syllabus luôn)
        return repo.save(p);
    }

    // Logic sinh mã giữ nguyên
    private String generateProjectCode() {
        ProjectSeq seq = seqRepo.lockById(1);
        if (seq == null) {
            seq = ProjectSeq.builder().id(1).nextVal(1L).build();
        }

        long current = (seq.getNextVal() == null ? 1L : seq.getNextVal());
        seq.setNextVal(current + 1);
        seqRepo.save(seq);

        return "PR" + String.format("%04d", current);
    }

    @Transactional(readOnly = true)
    public List<Project> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Project getById(String id) {
        // Chuyển String id sang Long vì Entity dùng @GeneratedValue Long
        try {
            Long projectId = Long.valueOf(id);
            return repo.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án: " + id));
        } catch (NumberFormatException e) {
            throw new RuntimeException("ID dự án không hợp lệ: " + id);
        }
    }

    @Transactional
    public Project submit(String id) {
        Project p = getById(id); // Đã có logic parse Long bên trong
        
        if (p.getStatus() != ProjectStatus.DRAFT && p.getStatus() != null) {
        throw new RuntimeException("Chỉ dự án ở trạng thái Nháp (DRAFT) mới có thể nộp duyệt");
    }   

        p.setStatus(ProjectStatus.PENDING);
        Project saved = repo.save(p);

        try {
            eventPublisher.publishProjectSubmitted(saved);
        } catch (Exception e) {
            log.error("Cảnh báo: Không thể gửi sự kiện nộp duyệt qua RabbitMQ: {}", e.getMessage());
        }

        return saved;
    }

    @Transactional
    public Project approve(String id) {
        Project p = getById(id);
        if (p.getStatus() != ProjectStatus.PENDING) {
            throw new RuntimeException("Chỉ dự án đang Chờ duyệt (PENDING) mới có thể phê duyệt");
        }

        p.setStatus(ProjectStatus.APPROVED);
        Project approvedProject = repo.save(p);

        try {
            eventPublisher.publishProjectApproved(approvedProject);
        } catch (Exception e) {
            log.error("Cảnh báo: Phê duyệt thành công nhưng không thể gửi thông báo RabbitMQ: {}", e.getMessage());
        }

        return approvedProject;
    }

    @Transactional
    public Project deny(String id) {
        Project p = getById(id);
        p.setStatus(ProjectStatus.DENIED);
        return repo.save(p);
    }

    @Transactional
    public Project assignToClass(String projectId, String classId) {
        Project p = getById(projectId);
        if (p.getStatus() != ProjectStatus.APPROVED) {
            throw new RuntimeException("Chỉ dự án đã được phê duyệt (APPROVED) mới có thể giao cho lớp");
        }

        // Lưu ý: Nếu classId trong DB là Long thì parse Long.valueOf(classId)
        // Nếu là String thì giữ nguyên.
        p.setClassId(classId); 
        p.setStatus(ProjectStatus.ASSIGNED);
        return repo.save(p);
    }

    public String extractSyllabusFromExcel(MultipartFile file) {
        // Cấu hình đường dẫn tới NiFi (Nơi bạn đặt Processor ListenHTTP hoặc HandleHttpRequest)
        String nifiUrl = "http://localhost:8081/api/nifi/extract-excel"; 

        try {
            // 1. Chuẩn bị Header & Body để gửi sang NiFi
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 2. Gửi file sang NiFi
            ResponseEntity<String> response = restTemplate.postForEntity(nifiUrl, requestEntity, String.class);

            // 3. Nhận kết quả Text đã xử lý từ NiFi trả về
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody(); 
            } else {
                throw new RuntimeException("NiFi trả về lỗi: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Lỗi khi gọi NiFi: {}", e.getMessage());
            // Fallback: Nếu chưa có NiFi, trả về giả lập để test Frontend
            return "Demo Syllabus Content: \n- Tuần 1: Nhập môn (Dữ liệu từ file " + file.getOriginalFilename() + ")";
        }
    }
}