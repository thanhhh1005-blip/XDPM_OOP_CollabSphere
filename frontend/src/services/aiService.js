// Đường dẫn: frontend/src/services/aiService.js

const API_URL = "http://localhost:8080/ai"; 

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