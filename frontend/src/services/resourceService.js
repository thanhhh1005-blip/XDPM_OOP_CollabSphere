import axios from 'axios';

// Đổi port 8084 nếu backend của bạn chạy port khác
const API_URL = "http://localhost:8084/api/resources";

// Cấu hình cứng tạm thời (Sau này sẽ lấy từ Login/AuthContext)
const CURRENT_USER = {
    id: "SV001",
    role: "USER" // Hoặc "STUDENT" tùy enum của bạn
};

const ResourceService = {
    // 1. Lấy danh sách file
    getResources: async (scope, scopeId) => {
        try {
            const response = await axios.get(API_URL, {
                params: { scope, scopeId }
            });
            return response.data.data; // Trả về mảng file
        } catch (error) {
            console.error("Lỗi lấy danh sách:", error);
            throw error;
        }
    },

    // 2. Upload file
    uploadFile: async (file, scope, scopeId) => {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("scope", scope);
        formData.append("scopeId", scopeId);
        formData.append("uploaderId", CURRENT_USER.id);
        formData.append("role", CURRENT_USER.role);

        try {
            const response = await axios.post(API_URL, formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            return response.data;
        } catch (error) {
            console.error("Lỗi upload:", error);
            throw error;
        }
    },

    // 3. Download file
    downloadFile: async (resourceId, fileName) => {
        try {
            const response = await axios.get(`${API_URL}/${resourceId}/download`, {
                responseType: 'blob', // Quan trọng: Báo cho axios biết đây là file binary
            });

            // Tạo link ảo để trình duyệt tự tải về
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName); // Đặt tên file khi tải về
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error("Lỗi download:", error);
        }
    },

    // 4. Xóa file
    deleteFile: async (resourceId) => {
        try {
            await axios.delete(`${API_URL}/${resourceId}`, {
                params: {
                    userId: CURRENT_USER.id,
                    role: CURRENT_USER.role
                }
            });
        } catch (error) {
            console.error("Lỗi xóa file:", error);
            throw error;
        }
    }
};

export default ResourceService;