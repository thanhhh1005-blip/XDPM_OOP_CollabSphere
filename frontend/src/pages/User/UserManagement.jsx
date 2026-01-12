import React, { useState, useEffect } from 'react';
import { getAllUsers, toggleUserStatus, importUsers } from '../../services/userService';

// --- ICONS (Màu tối cho nền sáng) ---
const SearchIcon = () => <svg className="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>;
const UploadIcon = () => <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" /></svg>;
const FilterIcon = () => <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" /></svg>;

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [isImportModalOpen, setIsImportModalOpen] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const res = await getAllUsers();
            setUsers(res.result || []); 
        } catch (error) {
            console.error("Lỗi tải danh sách:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchUsers(); }, []);

    const handleToggleStatus = async (user) => {
        // 👇 CẬP NHẬT: Dùng .active thay vì .isActive
        const newStatus = !user.active;
        
        // Optimistic Update: Cập nhật UI ngay lập tức
        const updatedList = users.map(u => u.id === user.id ? { ...u, active: newStatus } : u);
        setUsers(updatedList);

        try {
            await toggleUserStatus(user.id, newStatus);
        } catch (error) {
            alert("Lỗi cập nhật trạng thái");
            fetchUsers(); // Revert nếu lỗi
        }
    };

    const handleImport = async () => {
        if (!selectedFile) return;
        try {
            await importUsers(selectedFile);
            alert("Import thành công!");
            setIsImportModalOpen(false);
            fetchUsers();
        } catch (error) {
            alert("Import thất bại! Kiểm tra lại file.");
        }
    };

    const filteredUsers = users.filter(u => 
        u.username.toLowerCase().includes(searchTerm.toLowerCase()) || 
        u.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="p-6 md:p-8 min-h-screen font-sans bg-[#f5f5f5] text-gray-800">
            
            {/* --- HEADER --- */}
            <div className="flex flex-col md:flex-row justify-between items-end mb-8 gap-4 animate-fade-in-down">
                <div>
                    <h1 className="text-3xl font-extrabold text-[#1677ff]">
                        Quản Lý Người Dùng
                    </h1>
                    <p className="text-gray-500 mt-2 text-sm font-medium">Danh sách Giảng viên & Sinh viên</p>
                </div>
                
                <div className="flex gap-3">
                    <button 
                        onClick={() => setIsImportModalOpen(true)}
                        className="flex items-center gap-2 px-5 py-2.5 bg-white border border-gray-200 rounded-xl hover:border-[#1677ff] hover:text-[#1677ff] transition-all shadow-sm text-sm font-semibold text-gray-600 group"
                    >
                        <UploadIcon /> Import Excel
                    </button>
                </div>
            </div>

            {/* --- TOOLBAR (White Card) --- */}
            <div className="mb-6 bg-white border border-gray-200 p-4 rounded-2xl shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="relative w-full md:w-96">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                        <SearchIcon />
                    </div>
                    <input 
                        type="text" 
                        placeholder="Tìm kiếm theo tên, email..."
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                        className="w-full bg-gray-50 border border-gray-200 text-gray-800 text-sm rounded-xl pl-10 pr-4 py-2.5 focus:outline-none focus:border-[#1677ff] focus:ring-1 focus:ring-[#1677ff] transition-all placeholder-gray-400"
                    />
                </div>
                
                <div className="flex items-center gap-2 text-sm text-gray-500 font-medium">
                    <span className="flex items-center gap-1 px-3 py-1.5 bg-gray-100 rounded-lg border border-gray-200">
                        <FilterIcon /> {filteredUsers.length} Users
                    </span>
                </div>
            </div>

            {/* --- TABLE (White Card) --- */}
            <div className="overflow-hidden rounded-3xl border border-gray-200 shadow-lg bg-white">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-gray-50 border-b border-gray-200 text-xs text-gray-500 uppercase tracking-wider">
                                <th className="px-6 py-5 font-bold">User</th>
                                <th className="px-6 py-5 font-bold">Role</th>
                                <th className="px-6 py-5 font-bold">Status</th>
                                <th className="px-6 py-5 font-bold text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 text-sm">
                            {loading ? (
                                <tr><td colSpan="4" className="text-center py-10 text-gray-500 italic">Đang tải dữ liệu...</td></tr>
                            ) : filteredUsers.length === 0 ? (
                                <tr><td colSpan="4" className="text-center py-10 text-gray-500 italic">Không tìm thấy kết quả</td></tr>
                            ) : (
                                filteredUsers.map(user => (
                                    <tr key={user.id} className="hover:bg-blue-50/50 transition-colors duration-150 group">
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-4">
                                                <div className="w-10 h-10 rounded-full bg-[#1677ff]/10 flex items-center justify-center font-bold text-[#1677ff] shadow-sm">
                                                    {user.username.charAt(0).toUpperCase()}
                                                </div>
                                                <div>
                                                    <div className="font-bold text-gray-800">{user.username}</div>
                                                    <div className="text-xs text-gray-500">{user.email}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide border ${
                                                user.role === 'ADMIN' ? 'bg-red-50 text-red-600 border-red-200' : 
                                                user.role === 'TEACHER' ? 'bg-blue-50 text-blue-600 border-blue-200' : 
                                                'bg-green-50 text-green-600 border-green-200'
                                            }`}>
                                                {user.role}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-2">
                                                {/* 👇 CẬP NHẬT: Dùng .active */}
                                                <span className={`h-2.5 w-2.5 rounded-full ${user.active ? 'bg-green-500' : 'bg-gray-400'}`}></span>
                                                <span className={`text-xs font-semibold ${user.active ? 'text-green-700' : 'text-gray-500'}`}>
                                                    {user.active ? 'Active' : 'Disabled'}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <button 
                                                onClick={() => handleToggleStatus(user)}
                                                className={`text-xs font-bold px-4 py-2 rounded-lg border transition-all duration-200 ${
                                                    user.active 
                                                    ? 'border-red-200 text-red-600 hover:bg-red-50' 
                                                    : 'border-green-200 text-green-600 hover:bg-green-50'
                                                }`}
                                            >
                                                {/* 👇 CẬP NHẬT: Dùng .active */}
                                                {user.active ? 'Block' : 'Unblock'}
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* --- IMPORT MODAL --- */}
            {isImportModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4 animate-fade-in">
                    <div className="bg-white w-full max-w-lg p-8 rounded-3xl shadow-2xl transform transition-all scale-100">
                        <div className="text-center mb-6">
                            <div className="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4 text-[#1677ff]">
                                <UploadIcon />
                            </div>
                            <h3 className="text-2xl font-bold text-gray-800">Import Users</h3>
                            <p className="text-gray-500 text-sm mt-2">Tải lên file Excel (.xlsx) hoặc CSV.</p>
                        </div>
                        
                        <label className="flex flex-col items-center justify-center w-full h-40 border-2 border-dashed border-gray-300 rounded-2xl cursor-pointer hover:border-[#1677ff] hover:bg-blue-50 transition-all group bg-gray-50">
                            <div className="flex flex-col items-center justify-center pt-5 pb-6">
                                <p className="mb-2 text-sm text-gray-500 group-hover:text-[#1677ff] font-medium">
                                    {selectedFile ? (
                                        <span className="text-green-600 font-bold">{selectedFile.name}</span>
                                    ) : (
                                        "Click để chọn file hoặc kéo thả"
                                    )}
                                </p>
                            </div>
                            <input type="file" className="hidden" accept=".csv, .xlsx" onChange={e => setSelectedFile(e.target.files[0])} />
                        </label>

                        <div className="grid grid-cols-2 gap-4 mt-8">
                            <button onClick={() => setIsImportModalOpen(false)} className="px-4 py-3 text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl transition-colors text-sm font-bold">
                                Hủy bỏ
                            </button>
                            <button onClick={handleImport} className="px-4 py-3 bg-[#1677ff] hover:bg-blue-600 text-white rounded-xl font-bold shadow-lg transition-all text-sm">
                                Import Data
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserManagement;