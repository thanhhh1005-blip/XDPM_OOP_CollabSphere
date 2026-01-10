// authService.js
const API_URL = "http://localhost:8080/api/identity";

export const login = async (username, password) => {
    try {
        const response = await fetch(`${API_URL}/auth/token`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        // 👇 BƯỚC QUAN TRỌNG: Xử lý trường hợp Backend không trả về JSON (ví dụ lỗi 500, lỗi text)
        let data;
        try {
            data = await response.json();
        } catch (error) {
            // Nếu parse JSON thất bại -> Chứng tỏ Backend trả về Text hoặc lỗi Server
            throw new Error("Lỗi kết nối Server hoặc dữ liệu không hợp lệ.");
        }

        // Kiểm tra HTTP Status (ví dụ 400, 401, 500)
        if (!response.ok) {
            throw new Error(data.message || "Đăng nhập thất bại");
        }
        
        // Kiểm tra Logic Code của ApiResponse (ví dụ code 1001: Tài khoản bị khóa)
        if (data.code && data.code !== 1000) {
             throw new Error(data.message || "Đăng nhập thất bại");
        }

        return data; 
    } catch (error) {
        throw error;
    }
};

// 👇 CẬP NHẬT HÀM NÀY: Nhận thêm fullName
export const register = async (username, password, email, fullName) => {
    try {
        const response = await fetch(`${API_URL}/users`, { 
            method: "POST",
            headers: { "Content-Type": "application/json" },
            // 👇 Gửi thêm fullName vào body
            body: JSON.stringify({ username, password, email, fullName }),
        });
        
        // Cũng áp dụng try-catch JSON cho Register để an toàn
        let data;
        try {
            data = await response.json();
        } catch (error) {
            throw new Error("Lỗi kết nối Server hoặc dữ liệu không hợp lệ.");
        }

        if (!response.ok || (data.code && data.code !== 1000)) {
            throw new Error(data.message || "Đăng ký thất bại");
        }
        return data;
    } catch (error) {
        throw error;
    }
};