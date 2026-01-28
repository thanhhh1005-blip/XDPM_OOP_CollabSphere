package com.collab.workspaceservice.service;

import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;

    public List<Milestone> getByClassId(Long classId) {
        return milestoneRepository.findByClassIdOrderByEndDateAsc(classId);
    }

    public Milestone createManual(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }



    public List<Milestone> generateByAI(Long classId, String projectDescription) {
        List<Milestone> aiMilestones = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 1: Thấu cảm & Xác định vấn đề")
                .description("Tìm hiểu đối tượng thụ hưởng, phỏng vấn người dùng, xác định nỗi đau (Pain points). Mục tiêu: Hướng tới đóng góp xã hội.")
                .criteria("- 5 Phỏng vấn người dùng\n- Bản đồ thấu cảm (Empathy Map)")
                .startDate(now)
                .endDate(now.plusWeeks(2))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 2: Ý tưởng & Giải pháp")
                .description("Brainstorming các giải pháp công nghệ. Chọn giải pháp khả thi nhất giúp giải quyết vấn đề xã hội đã tìm ra.")
                .criteria("- Sơ đồ luồng người dùng (User Flow)\n- Wireframe sơ bộ")
                .startDate(now.plusWeeks(2))
                .endDate(now.plusWeeks(4))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 3: Phát triển MVP")
                .description("Xây dựng sản phẩm tối thiểu (MVP). Tập trung vào tính năng cốt lõi.")
                .criteria("- Source code trên Github\n- Demo sản phẩm chạy được")
                .startDate(now.plusWeeks(4))
                .endDate(now.plusWeeks(8))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        return milestoneRepository.saveAll(aiMilestones);
    }

    public Milestone getById(Long id) {
        return milestoneRepository.findById(id).orElse(null);
    }

    public Milestone save(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }

    public List<Milestone> generateFromSyllabus(Long classId, String syllabus, int weeks) {
        List<Milestone> milestones = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();


        milestones.add(Milestone.builder()
                .title("Giai đoạn 1: Phân tích & Lên ý tưởng")
                .description("Dựa trên đề cương: " + syllabus.substring(0, Math.min(syllabus.length(), 50)) + "...") // Trích 1 đoạn syllabus
                .criteria("- Hoàn thành SRS\n- Xác định Technology Stack")
                .startDate(now)
                .endDate(now.plusWeeks(2))
                .weekNumber(2) 
                .classId(classId)
                .createdBy("AI_ASSISTANT")
                .build());

        int midWeeks = weeks / 2;
        milestones.add(Milestone.builder()
                .title("Giai đoạn 2: Phát triển tính năng cốt lõi (MVP)")
                .description("Triển khai các chức năng chính. Tập trung vào Backend và Database.")
                .criteria("- API hoàn thiện 80%\n- Database Schema ổn định")
                .startDate(now.plusWeeks(2))
                .endDate(now.plusWeeks(midWeeks))
                .weekNumber(midWeeks)
                .classId(classId)
                .createdBy("AI_ASSISTANT")
                .build());


        milestones.add(Milestone.builder()
                .title("Giai đoạn 3: Kiểm thử & Báo cáo cuối kỳ")
                .description("Fix bugs, tối ưu UI/UX và chuẩn bị slide báo cáo.")
                .criteria("- Sản phẩm chạy mượt mà\n- Slide báo cáo đầy đủ")
                .startDate(now.plusWeeks(midWeeks))
                .endDate(now.plusWeeks(weeks))
                .weekNumber(weeks)
                .classId(classId)
                .createdBy("AI_ASSISTANT")
                .build());


        return milestoneRepository.saveAll(milestones);
    }
    public void delete(Long id) {
        if (milestoneRepository.existsById(id)) {
            milestoneRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy cột mốc để xóa");
        }
    }
    public List<Milestone> saveAll(List<Milestone> milestones) {
        return milestoneRepository.saveAll(milestones);
    }
    
}