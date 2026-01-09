import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';

// --- 1. Import các trang Auth (Từ code mới pull về) ---
import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";

// --- 2. Import các trang Education (Code của bạn) ---
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';

// --- 3. Import các trang chức năng khác ---
import AiPlanning from './pages/AI/AiPlanning';
import TaskBoard from './pages/Workspace/TaskBoard';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* === PHẦN AUTHENTICATION (Ưu tiên kiểm tra trước) === */}
        
        {/* Mặc định vào trang chủ (/) sẽ đá về trang Login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* Các trang Login/Register nằm độc lập (không có Header/Menu của MainLayout) */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />


        {/* === PHẦN GIAO DIỆN CHÍNH (Đã đăng nhập mới vào được) === */}
        {/* Layout này sẽ bao bọc các trang bên trong */}
        <Route element={<MainLayout />}>
            
            {/* -- Các trang Education của bạn -- */}
            <Route path="/subjects" element={<SubjectManager />} />
            <Route path="/classes" element={<ClassManager />} />

            {/* -- Các trang chức năng cũ -- */}
            <Route path="/ai-planning" element={<AiPlanning />} />
            <Route path="/workspace" element={<TaskBoard />} />

        </Route>


        {/* === XỬ LÝ LỖI === */}
        {/* Nếu nhập đường dẫn linh tinh không tồn tại -> Đá về Login */}
        <Route path="*" element={<Navigate to="/login" replace />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;