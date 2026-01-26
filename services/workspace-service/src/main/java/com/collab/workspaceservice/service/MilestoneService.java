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

    // 1. Lấy danh sách theo lớp
    public List<Milestone> getByClassId(Long classId) {
        return milestoneRepository.findByClassIdOrderByEndDateAsc(classId);
    }

    // 2. Tạo thủ công
    public Milestone createManual(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }


    // 4. LOGIC AI (Mô phỏng)
    // Sau này bạn sẽ thay đoạn này bằng cách gọi API OpenAI/Gemini
    public List<Milestone> generateByAI(Long classId, String projectDescription) {
        List<Milestone> aiMilestones = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Mốc 1: Khám phá & Thấu cảm (Tuần 1-2)
        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 1: Thấu cảm & Xác định vấn đề")
                .description("Tìm hiểu đối tượng thụ hưởng, phỏng vấn người dùng, xác định nỗi đau (Pain points). Mục tiêu: Hướng tới đóng góp xã hội.")
                .criteria("- 5 Phỏng vấn người dùng\n- Bản đồ thấu cảm (Empathy Map)")
                .startDate(now)
                .endDate(now.plusWeeks(2))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        // Mốc 2: Lên ý tưởng & Giải pháp (Tuần 3-4)
        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 2: Ý tưởng & Giải pháp")
                .description("Brainstorming các giải pháp công nghệ. Chọn giải pháp khả thi nhất giúp giải quyết vấn đề xã hội đã tìm ra.")
                .criteria("- Sơ đồ luồng người dùng (User Flow)\n- Wireframe sơ bộ")
                .startDate(now.plusWeeks(2))
                .endDate(now.plusWeeks(4))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        // Mốc 3: Phát triển & Kiểm thử (Tuần 5-8)
        aiMilestones.add(Milestone.builder()
                .title("Giai đoạn 3: Phát triển MVP")
                .description("Xây dựng sản phẩm tối thiểu (MVP). Tập trung vào tính năng cốt lõi.")
                .criteria("- Source code trên Github\n- Demo sản phẩm chạy được")
                .startDate(now.plusWeeks(4))
                .endDate(now.plusWeeks(8))
                .classId(classId)
                .createdBy("AI_BOT")
                .build());

        // Lưu vào DB luôn
        return milestoneRepository.saveAll(aiMilestones);
    }

    // ... Các code cũ giữ nguyên ...
    public Milestone getById(Long id) {
        return milestoneRepository.findById(id).orElse(null);
    }

    // 👇 THÊM HÀM NÀY: Để Controller gọi khi Update (Lưu lại)
    public Milestone save(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }
    // 👇 HÀM MỚI: Xử lý logic tạo Milestone từ Syllabus
    public List<Milestone> generateFromSyllabus(Long classId, String syllabus, int weeks) {
        List<Milestone> milestones = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // LOGIC GIẢ LẬP AI: Chia thời gian dựa trên tổng số tuần (weeks)
        // Ví dụ: weeks = 15
        
        // Giai đoạn 1: Khởi động (2 tuần đầu)
        milestones.add(Milestone.builder()
                .title("Giai đoạn 1: Phân tích & Lên ý tưởng")
                .description("Dựa trên đề cương: " + syllabus.substring(0, Math.min(syllabus.length(), 50)) + "...") // Trích 1 đoạn syllabus
                .criteria("- Hoàn thành SRS\n- Xác định Technology Stack")
                .startDate(now)
                .endDate(now.plusWeeks(2))
                .weekNumber(2) // Frontend bạn có dùng field này
                .classId(classId)
                .createdBy("AI_ASSISTANT")
                .build());

        // Giai đoạn 2: Phát triển Core (Giữa kỳ)
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

        // Giai đoạn 3: Hoàn thiện & Báo cáo (Cuối kỳ)
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

        // Lưu vào Database
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