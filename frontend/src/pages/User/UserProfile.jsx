import React, { useState, useEffect } from 'react';
import { updateProfile, changePassword, getMyInfo } from '../../services/userService';

// --- ICONS (Màu sắc phù hợp nền sáng) ---
const UserIcon = () => <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>;
const MailIcon = () => <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>;
const LockIcon = () => <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>;
const KeyIcon = () => <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11.5 14.5a9 9 0 00-6.73 6.73l-.73.73v-1.73l-1.5-1.5a2 2 0 01-.586-1.414V16a2 2 0 012-2h1.5l1.5-1.5 2-2z" /></svg>;

const UserProfile = () => {
    const [user, setUser] = useState({
        id: '',
        username: '',
        email: '',
        fullName: '',
        avatar: 'https://i.pravatar.cc/150?u=default',
        role: ''
    });

    const [passData, setPassData] = useState({ old: '', new: '', confirm: '' });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchMyProfile = async () => {
            try {
                const res = await getMyInfo();
                const userData = res.result || res; 
                
                setUser({
                    id: userData.id,
                    username: userData.username,
                    email: userData.email || 'Chưa cập nhật',
                    fullName: userData.firstName ? `${userData.firstName} ${userData.lastName}` : userData.username,
                    role: userData.roles ? userData.roles[0]?.name : 'USER',
                    avatar: 'https://i.pravatar.cc/150?u=' + userData.username
                });
            } catch (error) {
                console.error("Lỗi lấy thông tin:", error);
            }
        };
        fetchMyProfile();
    }, []);

    const handleUpdateInfo = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await updateProfile(user.id, { 
                firstName: user.fullName.split(' ')[0], 
                lastName: user.fullName.split(' ').slice(1).join(' '),
                email: user.email 
            });
            alert("Cập nhật thành công!");
        } catch (error) {
            alert("Lỗi cập nhật profile: " + (error.response?.data?.message || error.message));
        } finally { setLoading(false); }
    };

    const handleChangePass = async (e) => {
        e.preventDefault();
        if (passData.new !== passData.confirm) return alert("Mật khẩu xác nhận không khớp!");
        
        try {
            await changePassword(user.id, passData.old, passData.new);
            alert("Đổi mật khẩu thành công!");
            setPassData({ old: '', new: '', confirm: '' });
        } catch (error) {
            alert("Đổi mật khẩu thất bại: " + (error.response?.data?.message || "Sai mật khẩu cũ?"));
        }
    };

    return (
        <div className="p-4 md:p-8 min-h-screen font-sans bg-[#f5f5f5] text-gray-800">
            {/* Header Title */}
            <div className="mb-8 animate-fade-in-down">
                {/* 👇 MÀU TIÊU ĐỀ THEO YÊU CẦU */}
                <h1 className="text-3xl font-extrabold text-[#1677ff]">
                    Cài Đặt Tài Khoản
                </h1>
                <p className="text-gray-500 mt-1">Quản lý thông tin cá nhân và bảo mật</p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                
                {/* --- LEFT COLUMN: PROFILE CARD --- */}
                <div className="lg:col-span-4 space-y-6">
                    <div className="relative bg-white border border-gray-200 rounded-3xl overflow-hidden shadow-lg">
                        {/* Fake Cover Image (Màu xanh nhẹ cho hợp theme) */}
                        <div className="h-32 bg-gradient-to-r from-blue-400 to-indigo-500 opacity-90"></div>
                        
                        <div className="px-6 pb-8 text-center relative">
                            {/* Avatar */}
                            <div className="-mt-16 mb-4 inline-block p-1.5 rounded-full bg-white shadow-sm">
                                <img 
                                    src={user.avatar} 
                                    alt="Avatar" 
                                    className="w-28 h-28 rounded-full object-cover border-4 border-gray-50 shadow-md" 
                                />
                            </div>
                            
                            <h2 className="text-2xl font-bold text-gray-800 tracking-wide">{user.fullName}</h2>
                            <p className="text-[#1677ff] font-medium text-sm mt-1">@{user.username}</p>
                            
                            <div className="mt-4 flex justify-center gap-2">
                                <span className="px-4 py-1.5 rounded-full text-xs font-bold bg-blue-50 text-blue-600 border border-blue-100">
                                    {user.role}
                                </span>
                                <span className="px-4 py-1.5 rounded-full text-xs font-bold bg-green-50 text-green-600 border border-green-100">
                                    Active
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* --- RIGHT COLUMN: FORMS --- */}
                <div className="lg:col-span-8 space-y-6">
                    
                    {/* 1. EDIT PROFILE FORM */}
                    <div className="bg-white border border-gray-200 rounded-3xl p-8 shadow-lg">
                        <div className="flex items-center gap-3 mb-6 border-b border-gray-100 pb-4">
                            <div className="p-2 bg-blue-50 rounded-lg text-[#1677ff]">
                                <UserIcon />
                            </div>
                            <h3 className="text-xl font-bold text-gray-800">Thông tin cơ bản</h3>
                        </div>

                        <form onSubmit={handleUpdateInfo} className="space-y-5">
                            <div className="grid md:grid-cols-2 gap-5">
                                <div className="space-y-2">
                                    <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider ml-1">Họ và tên</label>
                                    <div className="relative group">
                                        <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-[#1677ff] transition-colors"><UserIcon /></div>
                                        <input 
                                            type="text" 
                                            value={user.fullName} 
                                            onChange={e => setUser({...user, fullName: e.target.value})} 
                                            className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-[#1677ff] focus:ring-1 focus:ring-[#1677ff] transition-all placeholder-gray-400"
                                            placeholder="Nhập họ tên..."
                                        />
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider ml-1">Email</label>
                                    <div className="relative group">
                                        <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-[#1677ff] transition-colors"><MailIcon /></div>
                                        <input 
                                            type="email" 
                                            value={user.email} 
                                            onChange={e => setUser({...user, email: e.target.value})} 
                                            className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-[#1677ff] focus:ring-1 focus:ring-[#1677ff] transition-all placeholder-gray-400"
                                            placeholder="example@mail.com"
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="flex justify-end pt-2">
                                <button disabled={loading} className="px-6 py-2.5 bg-[#1677ff] hover:bg-blue-600 text-white font-bold rounded-xl shadow-md transition-all duration-200 text-sm">
                                    {loading ? 'Đang lưu...' : 'Lưu Thay Đổi'}
                                </button>
                            </div>
                        </form>
                    </div>

                    {/* 2. CHANGE PASSWORD FORM */}
                    <div className="bg-white border border-gray-200 rounded-3xl p-8 shadow-lg">
                        <div className="flex items-center gap-3 mb-6 border-b border-gray-100 pb-4">
                            <div className="p-2 bg-red-50 rounded-lg text-red-500">
                                <KeyIcon />
                            </div>
                            <h3 className="text-xl font-bold text-gray-800">Bảo mật</h3>
                        </div>

                        <form onSubmit={handleChangePass} className="space-y-5">
                            <div className="space-y-2">
                                <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider ml-1">Mật khẩu hiện tại</label>
                                <div className="relative group">
                                    <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-red-500 transition-colors"><LockIcon /></div>
                                    <input 
                                        type="password" 
                                        value={passData.old} 
                                        onChange={e => setPassData({...passData, old: e.target.value})} 
                                        className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-red-500 focus:ring-1 focus:ring-red-500 transition-all placeholder-gray-400"
                                        placeholder="••••••••"
                                    />
                                </div>
                            </div>

                            <div className="grid md:grid-cols-2 gap-5">
                                <div className="space-y-2">
                                    <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider ml-1">Mật khẩu mới</label>
                                    <div className="relative group">
                                        <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-red-500 transition-colors"><KeyIcon /></div>
                                        <input 
                                            type="password" 
                                            value={passData.new} 
                                            onChange={e => setPassData({...passData, new: e.target.value})} 
                                            className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-[#1677ff] focus:ring-1 focus:ring-[#1677ff] transition-all placeholder-gray-400"
                                            placeholder="••••••••"
                                        />
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-semibold text-gray-500 uppercase tracking-wider ml-1">Xác nhận mật khẩu</label>
                                    <div className="relative group">
                                        <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-red-500 transition-colors"><KeyIcon /></div>
                                        <input 
                                            type="password" 
                                            value={passData.confirm} 
                                            onChange={e => setPassData({...passData, confirm: e.target.value})} 
                                            className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-3 focus:outline-none focus:border-[#1677ff] focus:ring-1 focus:ring-[#1677ff] transition-all placeholder-gray-400"
                                            placeholder="••••••••"
                                        />
                                    </div>
                                </div>
                            </div>
                            
                            <div className="flex justify-end pt-2">
                                <button className="px-6 py-2.5 border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 font-bold rounded-xl transition-all duration-200 text-sm shadow-sm">
                                    Cập Nhật Mật Khẩu
                                </button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default UserProfile;