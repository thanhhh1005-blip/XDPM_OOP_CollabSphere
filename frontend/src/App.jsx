import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';


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
import MilestonePage from './pages/Workspace/MilestonePage';
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';
import ProjectList from './pages/Projects/ProjectList'; 
import ProjectForm from './pages/Projects/ProjectForm';
import TeamList from './pages/Teams/TeamList';
import TeamCreate from './pages/Teams/TeamCreate';
import TeamDetail from './pages/Teams/TeamDetail';



function App() {
  return (
    <BrowserRouter>
      <Routes>

      

        {/* ===== Redirect mặc định ===== */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* ===== AUTH ===== */}

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Layout Chính */}
        <Route element={<MainLayout />}>
            {/* 👇 Route(sẽ hiển thị qua Outlet) */}
            <Route path="/users" element={<UserManagement />} />
            <Route path="/profile" element={<UserProfile />} />
            
            {/* 👇 Route của người khác (Khai báo để URL đẹp, MainLayout sẽ tự render component cứng) */}
            <Route path="/workspace" element={<TaskBoard />} />
            <Route path="/milestones" element={<MilestonePage />} />
            <Route path="/ai-planning" element={<AiPlanning />} />
            <Route path="/classes" element={<ClassManager />} />
            <Route path="/subjects" element={<SubjectManager />} />

            <Route path="/resources/*" element={<ResourcePage />} />
            <Route path="/collaborations/*" element={<CollaborationPage />} />
            <Route path="/projects" element={<ProjectList />} />   {/* ✅ ADD */}
            <Route path="/projects/new" element={<ProjectForm />} /> {/* ✅ ADD */}
            <Route path="/teams" element={<TeamList />} />
            <Route path="/teams/new" element={<TeamCreate />} />
            <Route path="/teams/:id" element={<TeamDetail />} />
        </Route>
{/* ===== Fallback ===== */}

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;