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

        const data = await response.json();

        // Logic kiểm tra lỗi:
        // Nếu response không OK, HOẶC (nếu có trường code mà code != 1000) -> Lỗi
        if (!response.ok) {
            throw new Error(data.message || "Đăng nhập thất bại");
        }
        
        if (data.code && data.code !== 1000) {
             throw new Error(data.message || "Đăng nhập thất bại");
        }

        return data; 
    } catch (error) {
        throw error;
    }
};

// ... Các hàm register, getMyInfo giữ nguyên ...
export const register = async (username, password, email) => {
    // ... code cũ của bạn ...
    // (Chỉ cần đảm bảo endpoint là /users khớp với Gateway)
    try {
        const response = await fetch(`${API_URL}/users`, { 
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password, email }),
        });
        const data = await response.json();
        if (!response.ok || (data.code && data.code !== 1000)) {
            throw new Error(data.message || "Đăng ký thất bại");
        }
        return data;
    } catch (error) {
        throw error;
    }
};