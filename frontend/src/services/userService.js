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

// 1. Lấy danh sách users
export const getAllUsers = async () => {
    const res = await axios.get(`${API_BASE}`, getConfig());
    return res.data; // Giả sử Backend trả về { result: [...] } hoặc trực tiếp mảng
};

// 2. Import Users (Batch Create)
export const importUsers = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    
    // Axios tự động set Content-Type là multipart/form-data
    const res = await axios.post(`${API_BASE}/import`, formData, getConfig());
    return res.data;
};

// 3. Khóa/Mở khóa tài khoản
export const toggleUserStatus = async (userId, isActive) => {
    const res = await axios.patch(`${API_BASE}/${userId}/status`, { isActive }, getConfig());
    return res.data;
};

// 4. Tạo user thủ công (Nếu cần)
export const createUser = async (userData) => {
    const res = await axios.post(`${API_BASE}`, userData, getConfig());
    return res.data;
};

// --- PERSONAL FEATURES ---

// 5. Cập nhật hồ sơ
export const updateProfile = async (userId, data) => {
    const res = await axios.put(`${API_BASE}/${userId}`, data, getConfig());
    return res.data;
};

// 6. Đổi mật khẩu
export const changePassword = async (userId, oldPassword, newPassword) => {
    const res = await axios.post(`${API_BASE}/${userId}/change-password`, { oldPassword, newPassword }, getConfig());
    return res.data;
};

export const getMyInfo = async () => {
    // Gọi vào endpoint /my-info (Bạn kiểm tra lại Controller xem đúng đường dẫn này chưa)
    const res = await axios.get(`${API_BASE}/my-info`, getConfig());
    return res.data; 
};