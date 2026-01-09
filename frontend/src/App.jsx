import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom'; // 1. Import thư viện Router
import MainLayout from './components/MainLayout';

// 2. Import các trang bạn vừa tạo
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';

// Import các trang cũ (Dựa trên cấu trúc thư mục bạn gửi)
import AiPlanning from './pages/AI/AiPlanning';
import TaskBoard from './pages/Workspace/TaskBoard';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* MainLayout sẽ là khung bao bên ngoài (Header + Menu) */}
        <Route path="/" element={<MainLayout />}>
           
           {/* --- CÁC TRANG MỚI (EDUCATION) --- */}
           <Route path="subjects" element={<SubjectManager />} />
           <Route path="classes" element={<ClassManager />} />
           
           {/* --- CÁC TRANG CŨ CỦA BẠN (Cấu hình sẵn luôn) --- */}
           <Route path="ai-planning" element={<AiPlanning />} />
           <Route path="workspace" element={<TaskBoard />} />

           {/* Route mặc định: Nếu vào trang chủ / thì hiện gì? 
               Bạn có thể để TaskBoard hoặc tạo trang Dashboard riêng */}
           <Route index element={<TaskBoard />} /> 

        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;