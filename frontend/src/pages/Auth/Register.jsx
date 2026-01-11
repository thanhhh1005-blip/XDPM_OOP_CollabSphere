import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
<<<<<<< HEAD
import { register } from '../../services/authService';
=======
import { register, loginWithGoogle } from '../../services/authService'; // 👈 Import thêm loginWithGoogle

// 👇 IMPORT FIREBASE & PROVIDER
import { auth } from '../../configs/firebase';
import { signInWithPopup, GoogleAuthProvider } from "firebase/auth";
>>>>>>> origin/main

// --- Icon Components ---
const UserIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
  </svg>
);
<<<<<<< HEAD
// 👇 Icon mới cho Họ tên
=======
>>>>>>> origin/main
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
<<<<<<< HEAD
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
=======
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
  </svg>
);

// 👇 Icon Google (MỚI)
const GoogleIcon = () => (
  <svg className="w-5 h-5 mr-2" viewBox="0 0 48 48">
    <path fill="#FFC107" d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z" />
    <path fill="#FF3D00" d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z" />
    <path fill="#4CAF50" d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z" />
    <path fill="#1976D2" d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z" />
>>>>>>> origin/main
  </svg>
);

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
<<<<<<< HEAD
    fullName: '', // 👈 Thêm state fullName
=======
    fullName: '', 
>>>>>>> origin/main
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

<<<<<<< HEAD
=======
  // 1. Logic Đăng ký thường (GIỮ NGUYÊN)
>>>>>>> origin/main
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
<<<<<<< HEAD
      // 👇 Gửi thêm fullName vào hàm register
=======
>>>>>>> origin/main
      await register(formData.username, formData.password, formData.email, formData.fullName);
      alert("Đăng ký thành công! Vui lòng đăng nhập.");
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Đăng ký thất bại.');
    } finally {
      setLoading(false);
    }
  };

<<<<<<< HEAD
=======
  // 2. Logic Đăng ký bằng Google (MỚI THÊM)
  const handleGoogleRegister = async () => {
    try {
      setLoading(true);
      setError('');

      // Tạo Provider mới -> Luôn hiện bảng chọn tài khoản
      const provider = new GoogleAuthProvider();
      provider.setCustomParameters({
        prompt: 'select_account'
      });

      // Mở Popup Google
      const result = await signInWithPopup(auth, provider);
      const idToken = await result.user.getIdToken();

      // Gửi token xuống Backend
      const data = await loginWithGoogle(idToken);
      
      const token = data.result ? data.result.token : data.token;
      if (token) {
         localStorage.setItem('token', token);
         // Vì là đăng ký nhanh qua Google, ta cho vào Workspace luôn
         navigate('/workspace'); 
      }
    } catch (err) {
      console.error(err);
      setError("Đăng ký bằng Google thất bại!");
    } finally {
      setLoading(false);
    }
  };

>>>>>>> origin/main
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
<<<<<<< HEAD
      <div className="relative z-10 flex flex-col items-center justify-center w-full transform scale-90 md:scale-100 transition-transform duration-500">
=======
      <div className="relative z-10 flex flex-col items-center justify-center w-full transform scale-90 transition-transform duration-500">
>>>>>>> origin/main
          
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

<<<<<<< HEAD
              {/* --- Input 2: Full Name (MỚI THÊM) --- */}
=======
              {/* --- Input 2: Full Name --- */}
>>>>>>> origin/main
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

<<<<<<< HEAD
=======
              {/* 👇 PHẦN NÚT GOOGLE MỚI 👇 */}
              <div className="flex items-center my-1">
                 <div className="flex-1 border-t border-white/20"></div>
                 <span className="px-3 text-white/50 text-[10px] font-medium">HOẶC</span>
                 <div className="flex-1 border-t border-white/20"></div>
              </div>

              <button 
                type="button" 
                onClick={handleGoogleRegister}
                disabled={loading}
                className="w-full bg-white text-gray-700 font-bold py-2 rounded-lg text-xs shadow-md hover:bg-gray-50 transition-all flex items-center justify-center gap-2 transform active:scale-[0.98]"
              >
                 <GoogleIcon />
                 <span>Đăng ký bằng Google</span>
              </button>
              {/* 👆 KẾT THÚC PHẦN NÚT GOOGLE 👆 */}

>>>>>>> origin/main
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