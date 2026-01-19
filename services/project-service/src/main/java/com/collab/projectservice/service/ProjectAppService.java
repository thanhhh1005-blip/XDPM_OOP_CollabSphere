package com.collab.projectservice.service;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.domain.ProjectStatus;
import com.collab.projectservice.domain.ProjectSeq;
import com.collab.projectservice.repo.ProjectRepository;
import com.collab.projectservice.repo.ProjectSeqRepository; // ✅ ADD
import com.collab.projectservice.infra.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectAppService {

    private final ProjectRepository repo;
    private final EventPublisher eventPublisher;
    private final ProjectSeqRepository seqRepo; // ✅ ADD

    @Transactional
    public Project create(String title, String description, String syllabusId) {
        String projectCode = generateProjectCode(); // ✅ ADD

        Project p = Project.builder()
                .projectCode(projectCode)          // ✅ ADD
                .title(title)
                .description(description)
                .syllabusId(syllabusId)
                .status(ProjectStatus.DRAFT)
                .build();
        return repo.save(p);
    }

    private String generateProjectCode() {
        ProjectSeq seq = seqRepo.lockById(1);
        if (seq == null) {
            // phòng khi DB chưa insert row seq
            seq = ProjectSeq.builder().id(1).nextVal(1L).build();
        }

        long current = (seq.getNextVal() == null ? 1L : seq.getNextVal());
        seq.setNextVal(current + 1);
        seqRepo.save(seq);

        // PR0001, PR0002...
        return "PR" + String.format("%04d", current);
    }

    @Transactional(readOnly = true)
    public List<Project> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Project getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án: " + id));
    }

    @Transactional
    public Project submit(String id) {
        Project p = getById(id);
        if (p.getStatus() != ProjectStatus.DRAFT) {
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

        p.setClassId(classId);
        p.setStatus(ProjectStatus.ASSIGNED);
        return repo.save(p);
    }
}
