// Đường dẫn: frontend/src/services/aiService.js

// Giữ nguyên Gateway URL cũ của bạn
const API_URL = "http://localhost:8080/api/ai"; 

// --- 1. Hàm tạo kế hoạch (GIỮ NGUYÊN LOGIC CŨ) ---
export const generateMilestones = async (syllabusContent) => {
  try {
    const response = await fetch(`${API_URL}/generate-milestones`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      // Gửi đúng key 'syllabus' như trong Java DTO
      body: JSON.stringify({ syllabus: syllabusContent }),
    });

    if (!response.ok) {
      throw new Error("Lỗi kết nối đến Server");
    }

    const result = await response.json();
    return result; 
  } catch (error) {
    console.error("Lỗi AI Service:", error);
    throw error;
  }
};

// --- 2. Hàm lưu log (GIỮ NGUYÊN LOGIC CŨ) ---
export const saveAiLog = async (syllabus, jsonResult) => {
  try {
    const response = await fetch(`${API_URL}/save-log`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ 
        syllabus: syllabus,
        jsonResult: jsonResult 
      }),
    });

    if (!response.ok) {
      throw new Error("Lỗi khi lưu");
    }

    return await response.json();
  } catch (error) {
    console.error("Lỗi Save Service:", error);
    throw error;
  }
};

// --- 3. Hàm lấy lịch sử (CẬP NHẬT ĐỂ CHẠY QUA GATEWAY) ---
export const getHistory = async () => {
    try {
        // Sử dụng fetch thay axios để đồng bộ
        // Gọi vào: http://localhost:8080/ai/history
        // (Khớp với Controller Java đã sửa là @RequestMapping("/ai/history"))
        const response = await fetch(`${API_URL}/history`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        });

        if (!response.ok) {
            throw new Error("Không lấy được lịch sử từ Gateway");
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Lỗi lấy lịch sử:", error);
        return []; // Trả về mảng rỗng để không crash giao diện
    }
};