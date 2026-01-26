package com.collabsphere.aiservice.service;

import com.collab.shared.dto.MilestoneGenResponse; // Import DTO từ module shared
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneGeneratorService {

    // Thay AiClient bằng GeminiService có sẵn của bạn
    private final GeminiService geminiService; 
    private final ObjectMapper objectMapper; // Để parse JSON string thành Object

    public List<MilestoneGenResponse> generateFromSyllabus(String syllabus, int weeks) {
        String prompt = String.format("""
            Bạn là một trợ lý giáo dục chuyên nghiệp cho hệ thống PBL (Project-Based Learning).
            Dựa vào nội dung Syllabus dưới đây, hãy tạo ra một danh sách các Milestones (Cột mốc) cho dự án kéo dài %d tuần.
            
            Yêu cầu:
            1. Chia nhỏ dự án thành 3-5 milestones chính logic.
            2. Trả về kết quả CHỈ LÀ MỘT JSON ARRAY thuần túy, không có markdown (không dùng ```json), không giải thích thêm.
            3. Định dạng JSON: [{"title": "...", "description": "...", "weekNumber": 5, "criteria": "..."}]
            
            Nội dung Syllabus:
            %s
            """, weeks, syllabus);

        // Gọi Gemini
        String jsonResponse = geminiService.callGemini(prompt); 

        // Parse String thành List Object
        return parseJsonToMilestones(jsonResponse);
    }
    
    private List<MilestoneGenResponse> parseJsonToMilestones(String json) {
        try {
            // Làm sạch chuỗi JSON nếu AI trả về kèm markdown
            String cleanJson = json.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanJson, new TypeReference<List<MilestoneGenResponse>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về rỗng nếu lỗi
        }
    }
}