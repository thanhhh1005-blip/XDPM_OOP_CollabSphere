import axios from "axios";

// Đảm bảo đường dẫn này đúng (v1/classes)
const API_BASE = "http://localhost:8080/api/classes"; 

const getConfig = () => {
    const token = localStorage.getItem('token');
    return {
        headers: { 
            Authorization: `Bearer ${token}`
        }
    };
};

// --- CÁC HÀM API ---

export const getAllClasses = async () => {
    try {
        const res = await axios.get(`${API_BASE}`, getConfig());
        console.log("🔥 API getAllClasses Response:", res.data);

        // 👇 LOGIC FIX: Nếu server trả về mảng trực tiếp (Array) thì dùng luôn
        if (Array.isArray(res.data)) {
            return res.data;
        }

        // Nếu server trả về object { result: [...] } (kiểu ApiResponse chuẩn)
        return res.data.result || res.data.data || []; 
    } catch (error) {
        console.error("🔥 Lỗi gọi API getAllClasses:", error);
        return []; // Trả về mảng rỗng để không bị crash trang web
    }
};

export const createClass = async (classData) => {
    const res = await axios.post(`${API_BASE}`, classData, getConfig());
    return res.data;
};

export const updateClass = async (id, classData) => {
    const res = await axios.put(`${API_BASE}/${id}`, classData, getConfig());
    return res.data;
};

export const deleteClass = async (id) => {
    const res = await axios.delete(`${API_BASE}/${id}`, getConfig());
    return res.data;
};

// --- QUẢN LÝ SINH VIÊN ---

export const getStudentsInClass = async (classId) => {
    try {
        const res = await axios.get(`${API_BASE}/${classId}/students`, getConfig());
        console.log(`🔥 Students in Class ${classId}:`, res.data);
        
        // 👇 LOGIC FIX TƯƠNG TỰ
        if (Array.isArray(res.data)) {
            return res.data;
        }
        return res.data.result || res.data.data || [];
    } catch (error) {
        console.error("Lỗi lấy danh sách sinh viên:", error);
        return [];
    }
};

export const addStudentToClass = async (classId, studentId) => {
    const res = await axios.post(`${API_BASE}/${classId}/students/${studentId}`, {}, getConfig());
    return res.data;
};

export const removeStudentFromClass = async (classId, studentId) => {
    const res = await axios.delete(`${API_BASE}/${classId}/students/${studentId}`, getConfig());
    return res.data;
};

export const importClasses = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    
    const config = getConfig();
    config.headers["Content-Type"] = "multipart/form-data";

    const res = await axios.post(`${API_BASE}/import`, formData, config);
    return res.data;
};