import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Login from "./pages/Auth/Login";
import AiPlanning from "./pages/AI/AiPlanning";
import Register from "./pages/Auth/Register";
// import TaskBoard from "./pages/Workspace/TaskBoard"; // Import các trang khác của bạn ở đây

function App() {
  return (
    <Router>
      <Routes>
        {/* 1. Mặc định vào trang chủ sẽ đá về trang Login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* 2. Route cho trang Login (Độc lập, không dính Layout chung) */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        {/* 3. Route cho các trang bên trong (Bọc bởi MainLayout) */}
        <Route path="/" element={<MainLayout />}>
          {/* Khi login xong, vào /workspace sẽ hiện ra layout + nội dung bên trong */}
          {/* <Route path="workspace" element={<TaskBoard />} /> */}
          
          {/* Ví dụ trang AI Planning */}
          <Route path="ai-planning" element={<AiPlanning />} />
        </Route>

        {/* 4. Xử lý trang 404 (Nếu nhập linh tinh thì quay về login) */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;