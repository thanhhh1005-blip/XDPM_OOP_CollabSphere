import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../../services/authService';

// --- Icon Components ---
const UserIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
  </svg>
);
// 👇 Icon mới cho Họ tên
const IdIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M10 6H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V8a2 2 0 00-2-2h-5m-4 0V5a2 2 0 114 0v1m-4 0c0 .884-.5 2-2 2h4c-1.5 0-2-1.116-2-2z" />
  </svg>
);
const EmailIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
  </svg>
);
const LockIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
  </svg>
);

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    fullName: '', // 👈 Thêm state fullName
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError("Mật khẩu nhập lại không khớp!");
      setLoading(false);
      return;
    }

    try {
      // 👇 Gửi thêm fullName vào hàm register
      await register(formData.username, formData.password, formData.email, formData.fullName);
      alert("Đăng ký thành công! Vui lòng đăng nhập.");
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Đăng ký thất bại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative h-screen w-screen flex flex-col items-center justify-center overflow-hidden font-sans bg-gray-900">
      
      {/* Background */}
      <div 
        className="absolute inset-0 z-0 bg-cover bg-center"
        style={{ backgroundImage: `url('https://i.postimg.cc/0jv27Qvw/20008380-645221846.jpg')` }}
      >
        <div className="absolute inset-0 bg-black/40"></div>
      </div>

      {/* Container */}
      <div className="relative z-10 flex flex-col items-center justify-center w-full transform scale-90 md:scale-100 transition-transform duration-500">
          
          {/* Logo */}
          <div className="mb-4 text-center animate-fade-in-up">
            <h1 className="text-4xl md:text-5xl font-extrabold text-white tracking-[0.2em] uppercase drop-shadow-xl opacity-95">
              CollabSphere
            </h1>
          </div>

          {/* Form Card */}
          <div className="w-[340px] md:w-[400px] p-6 bg-white/10 border border-white/20 rounded-2xl backdrop-blur-xl shadow-2xl mx-4">
            
            <form onSubmit={handleRegister} className="flex flex-col gap-3">
              <h2 className="text-xl text-white font-bold uppercase text-center mb-1">Tạo Tài Khoản</h2>

              {error && (
                <div className="p-2 bg-red-500/80 text-white rounded text-xs text-center shadow-md animate-bounce-short">
                  ⚠️ {error}
                </div>
              )}

              {/* --- Input 1: Username --- */}
              <div className="flex flex-col items-start">
                <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">Tên đăng nhập</label>
                <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                  <span className="absolute left-3"><UserIcon /></span>
                  <input 
                    type="text" id="username" required 
                    value={formData.username} onChange={handleChange}
                    className="w-full bg-transparent text-white text-sm px-10 py-2.5 outline-none placeholder-white/30"
                    placeholder="Chọn tên hiển thị" autoComplete="off"
                  />
                </div>
              </div>

              {/* --- Input 2: Full Name (MỚI THÊM) --- */}
              <div className="flex flex-col items-start">
                <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">Họ và tên</label>
                <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                  <span className="absolute left-3"><IdIcon /></span>
                  <input 
                    type="text" id="fullName" required 
                    value={formData.fullName} onChange={handleChange}
                    className="w-full bg-transparent text-white text-sm px-10 py-2.5 outline-none placeholder-white/30"
                    placeholder="Nhập họ tên đầy đủ" autoComplete="off"
                  />
                </div>
              </div>

              {/* --- Input 3: Email --- */}
              <div className="flex flex-col items-start">
                <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">Email</label>
                <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                  <span className="absolute left-3"><EmailIcon /></span>
                  <input 
                    type="email" id="email" required 
                    value={formData.email} onChange={handleChange}
                    className="w-full bg-transparent text-white text-sm px-10 py-2.5 outline-none placeholder-white/30"
                    placeholder="name@example.com" autoComplete="off"
                  />
                </div>
              </div>

              {/* --- Input 4: Password --- */}
              <div className="flex flex-col items-start">
                <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">Mật khẩu</label>
                <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                  <span className="absolute left-3"><LockIcon /></span>
                  <input 
                    type="password" id="password" required 
                    value={formData.password} onChange={handleChange}
                    className="w-full bg-transparent text-white text-sm px-10 py-2.5 outline-none placeholder-white/30"
                    placeholder="••••••••"
                  />
                </div>
              </div>

              {/* --- Input 5: Confirm Password --- */}
              <div className="flex flex-col items-start">
                <label className="text-white/80 text-xs font-bold uppercase ml-1 mb-1">Nhập lại mật khẩu</label>
                <div className="relative w-full flex items-center bg-white/5 border border-white/20 rounded-lg focus-within:border-white/60 focus-within:bg-white/10 transition-all duration-300">
                  <span className="absolute left-3"><LockIcon /></span>
                  <input 
                    type="password" id="confirmPassword" required 
                    value={formData.confirmPassword} onChange={handleChange}
                    className="w-full bg-transparent text-white text-sm px-10 py-2.5 outline-none placeholder-white/30"
                    placeholder="••••••••"
                  />
                </div>
              </div>

              {/* Terms */}
              <label className="flex items-center cursor-pointer hover:text-white transition-colors mt-1">
                <input type="checkbox" required className="accent-[#ffdde1] mr-2 w-4 h-4 cursor-pointer" />
                <span className="text-white/80 text-xs">Đồng ý với điều khoản sử dụng</span>
              </label>

              {/* Submit Button */}
              <button 
                type="submit" disabled={loading}
                className={`w-full mt-2 bg-gradient-to-r from-[#271930] to-[#513661] text-white font-bold py-2.5 rounded-lg text-sm border border-white/10 shadow-lg hover:shadow-white/20 transition-all duration-300 transform active:scale-[0.98]
                          ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
              >
                {loading ? 'Đang xử lý...' : 'ĐĂNG KÝ NGAY'}
              </button>

              {/* Login Link */}
              <div className="text-center text-white/80 text-xs mt-1">
                <span>Đã có tài khoản? </span>
                <span onClick={() => navigate('/login')} className="text-[#ffdde1] font-bold hover:underline hover:text-white cursor-pointer ml-1">
                  Đăng nhập
                </span>
              </div>

            </form>
          </div>
      </div>
    </div>
  );
};

export default Register;