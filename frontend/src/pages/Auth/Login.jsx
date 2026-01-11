import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../services/authService";
import { Form, Input, Button, Checkbox, message } from "antd";

// --- ICONS SVG ---
const UserIcon = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    className="h-5 w-5 text-white/60"
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={1.5}
      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
    />
  </svg>
);

const LockIcon = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    className="h-5 w-5 text-white/60"
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={1.5}
      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
    />
  </svg>
);

const Login = () => {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const data = await login(username, password);

      const token = data.result ? data.result.token : data.token;
      const userFromDB = data.result || data; 

      if (token) {
        localStorage.setItem("token", token);
        const authResponse = data.result ? data.result : data;
        const userInfo = authResponse.user;
        if (userInfo) {
            const userToStore = {
              id: userInfo.id,
              username: userInfo.username,
              fullName: userInfo.fullName,
              role: userInfo.role, // Lấy đúng từ trong túi 'user'
              avatarUrl: userInfo.avatarUrl,
            };
            localStorage.setItem("user", JSON.stringify(userToStore));
            message.success(`Chào mừng ${userToStore.fullName} quay trở lại!`);
            navigate("/workspace");
        } else {
            // Trường hợp backend chưa trả về user (fallback)
            setError("Lỗi: Backend chưa trả về thông tin User!");
        }
      } else {
        setError("Không nhận được token xác thực.");
      }
    } catch (err) {
      console.error("Login Error:", err);
      if (err.message && err.message.includes("Unexpected end of JSON")) {
        setError(
          "Lỗi kết nối Server (CORS/Gateway). Vui lòng kiểm tra lại Backend."
        );
      } else {
        setError(err.message || "Tên đăng nhập hoặc mật khẩu không đúng.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative h-screen w-screen flex flex-col items-center justify-center overflow-hidden font-sans bg-gray-900">
      {/* --- BACKGROUND --- */}
      <div
        className="absolute inset-0 z-0 bg-cover bg-center"
        style={{
          backgroundImage: `url('https://i.postimg.cc/0jv27Qvw/20008380-645221846.jpg')`,
        }}
      >
        <div className="absolute inset-0 bg-black/40"></div>
      </div>

      {/* --- CONTENT --- */}
      <div className="relative z-10 flex flex-col items-center justify-center w-full transform scale-90 md:scale-100 transition-transform duration-500">
        {/* BRAND NAME */}
        <div className="mb-4 text-center animate-fade-in-up">
          <h1 className="text-5xl md:text-7xl font-extrabold text-white tracking-[0.2em] uppercase drop-shadow-xl opacity-95">
            CollabSphere
          </h1>
          <p className="text-white/80 text-sm md:text-lg mt-2 tracking-widest font-light">
            NỀN TẢNG QUẢN LÝ KHÔNG GIAN LÀM VIỆC TIỆN LỢI
          </p>
        </div>

        {/* GLASS FORM (Style mới: Minimalist with Icons) */}
        <div className="w-[340px] md:w-[380px] p-8 bg-white/10 border border-white/20 rounded-2xl backdrop-blur-xl shadow-2xl mx-4">
          <form onSubmit={handleLogin} className="flex flex-col gap-4">
            <h2 className="text-2xl text-white font-bold uppercase text-center mb-2">
              Đăng Nhập
            </h2>

            {/* Báo lỗi */}
            {error && (
              <div className="p-3 bg-red-500/80 text-white rounded-lg text-xs text-center shadow-md animate-bounce-short">
                ⚠️ {error}
              </div>
            )}

            {/* --- Input 1: Username --- */}
            <div className="flex flex-col items-start">
              <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">
                Tên đăng nhập
              </label>
              <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                <span className="absolute left-3">
                  <UserIcon />
                </span>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full bg-transparent text-white text-sm px-10 py-3 outline-none placeholder-white/30"
                  placeholder="Nhập tên đăng nhập"
                  autoComplete="off"
                />
              </div>
            </div>

            {/* --- Input 2: Password --- */}
            <div className="flex flex-col items-start">
              <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">
                Mật khẩu
              </label>
              <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                <span className="absolute left-3">
                  <LockIcon />
                </span>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-transparent text-white text-sm px-10 py-3 outline-none placeholder-white/30"
                  placeholder="Nhập mật khẩu"
                />
              </div>
            </div>

            {/* Options: Remember & Forgot Pass */}
            <div className="flex items-center justify-between text-white/80 text-xs mt-1">
              <label className="flex items-center cursor-pointer hover:text-white transition-colors">
                <input
                  type="checkbox"
                  className="accent-[#ffdde1] mr-1.5 cursor-pointer w-3.5 h-3.5"
                />
                <span>Ghi nhớ tôi</span>
              </label>
              <a
                href="#"
                className="hover:text-[#ffdde1] hover:underline transition-colors font-medium"
              >
                Quên mật khẩu?
              </a>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className={`w-full mt-2 bg-gradient-to-r from-[#271930] to-[#513661] text-white font-bold py-3 rounded-lg text-sm border border-white/10 shadow-lg 
                          hover:from-[#3e2a4a] hover:to-[#6a4c7d] hover:shadow-white/20 transition-all duration-300 transform active:scale-[0.98]
                          ${loading ? "opacity-70 cursor-not-allowed" : ""}`}
            >
              {loading ? "Đang xác thực..." : "ĐĂNG NHẬP"}
            </button>

            {/* Register Link */}
            <div className="text-center text-white/80 text-xs mt-2">
              <span>Chưa có tài khoản? </span>
              <span
                onClick={() => navigate("/register")}
                className="text-[#ffdde1] font-bold hover:underline hover:text-white transition-colors cursor-pointer ml-1"
              >
                Đăng ký ngay
              </span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
