import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
<<<<<<< HEAD

import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";
import UserManagement from './pages/User/UserManagement';
import UserProfile from './pages/User/UserProfile';

// Các trang khác (Để giữ cho Router không bị lỗi 404, dù MainLayout đã render cứng rồi thì khai báo ở đây cũng không thừa)
import AiPlanning from './pages/AI/AiPlanning';
import TaskBoard from './pages/Workspace/TaskBoard';
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';
import ProjectList from './pages/Projects/ProjectList'; 
import ProjectForm from './pages/Projects/ProjectForm';

=======

/* ===================== AUTH (CHUNG) ===================== */
import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";
/* ===================== RESOURCE ===================== */
import ResourcePage from './pages/Resource';
import CollaborationPage from './pages/Collaboration';

/* ===================== USER MANAGEMENT ===================== */
import UserManagement from './pages/User/UserManagement';
import UserProfile from './pages/User/UserProfile';

// Các trang khác (Để giữ cho Router không bị lỗi 404, dù MainLayout đã render cứng rồi thì khai báo ở đây cũng không thừa)
import AiPlanning from './pages/AI/AiPlanning';
import TaskBoard from './pages/Workspace/TaskBoard';
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';
>>>>>>> origin/main

function App() {
  return (
    <BrowserRouter>
      <Routes>
<<<<<<< HEAD
        <Route path="/" element={<Navigate to="/login" replace />} />
=======
        {/* ===== Redirect mặc định ===== */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* ===== AUTH ===== */}
>>>>>>> origin/main
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Layout Chính */}
        <Route element={<MainLayout />}>
            {/* 👇 Route(sẽ hiển thị qua Outlet) */}
            <Route path="/users" element={<UserManagement />} />
            <Route path="/profile" element={<UserProfile />} />
            
            {/* 👇 Route của người khác (Khai báo để URL đẹp, MainLayout sẽ tự render component cứng) */}
            <Route path="/workspace" element={<TaskBoard />} />
            <Route path="/ai-planning" element={<AiPlanning />} />
            <Route path="/classes" element={<ClassManager />} />
            <Route path="/subjects" element={<SubjectManager />} />
<<<<<<< HEAD
            <Route path="/projects" element={<ProjectList />} />   {/* ✅ ADD */}
            <Route path="/projects/new" element={<ProjectForm />} /> {/* ✅ ADD */}
        </Route>

=======
            <Route path="/resources/*" element={<ResourcePage />} />
            <Route path="/collaborations/*" element={<CollaborationPage />} />
        </Route>
{/* ===== Fallback ===== */}
>>>>>>> origin/main
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;