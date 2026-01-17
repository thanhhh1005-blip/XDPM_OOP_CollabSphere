import axios from 'axios';

const API_BASE = "http://localhost:8080/api/identity/users";

// Helper lấy header (Token)
const getConfig = () => {
    const token = localStorage.getItem('token');
    return {
        headers: { Authorization: `Bearer ${token}` }
    };
};

// --- ADMIN FEATURES ---

export const getAllUsers = async () => {
    const res = await axios.get(`${API_BASE}`, getConfig());
    return res.data; 
};

export const importUsers = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await axios.post(`${API_BASE}/import`, formData, getConfig());
    return res.data;
};

// 👇 CẬP NHẬT QUAN TRỌNG: Gửi key "active" thay vì "isActive"
export const toggleUserStatus = async (userId, status) => {
    // Backend (UserStatusRequest) đợi biến "active", nên ta phải gửi { active: status }
    const res = await axios.patch(`${API_BASE}/${userId}/status`, { active: status }, getConfig());
    return res.data;
};

export const createUser = async (userData) => {
    const res = await axios.post(`${API_BASE}`, userData, getConfig());
    return res.data;
};

// --- PERSONAL FEATURES ---

export const updateProfile = async (userId, data) => {
    const res = await axios.put(`${API_BASE}/${userId}`, data, getConfig());
    return res.data;
};

export const changePassword = async (userId, oldPassword, newPassword) => {
    const res = await axios.post(`${API_BASE}/${userId}/change-password`, { oldPassword, newPassword }, getConfig());
    return res.data;
};

export const getMyInfo = async () => {
    const res = await axios.get(`${API_BASE}/my-info`, getConfig());
    return res.data; 
};

// 👇 HÀM MỚI: Lấy danh sách Giảng viên (Role = LECTURER)
// Backend API: GET /users/role/LECTURER
export const getLecturers = async () => {
    try {
        // API_BASE đang là ".../users", ta nối thêm "/role/LECTURER"
        const res = await axios.get(`${API_BASE}/role/LECTURER`, getConfig());
        
        // Backend trả về: { code: 1000, result: [...] } -> Ta lấy .result
        return res.data.result; 
    } catch (error) {
        console.error("Lỗi khi lấy danh sách giảng viên:", error);
        return []; // Trả về mảng rỗng để không bị lỗi màn hình
    }
};

// 👇 HÀM MỚI: Lấy danh sách Sinh Viên (Role = STUDENT)
export const getStudents = async () => {
    try {
        const res = await axios.get(`${API_BASE}/role/STUDENT`, getConfig());
        return res.data.result; 
    } catch (error) {
        console.error("Lỗi lấy danh sách sinh viên:", error);
        return [];
    }
};