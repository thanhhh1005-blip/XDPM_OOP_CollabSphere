import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';

/* ===================== AUTH (CHUNG) ===================== */
import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";

/* ===================== RESOURCE (QUAN TRỌNG: SỬA IMPORT) ===================== */
// Lưu ý: Phải trỏ đúng vào file ResourcePage.jsx
import ResourcePage from './pages/Resource/ResourcePage'; 

/* ===================== USER MANAGEMENT ===================== */
import UserManagement from './pages/User/UserManagement';
import UserProfile from './pages/User/UserProfile';

// Các trang khác
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
import CollaborationPage from './pages/Collaboration/CollaborationPage'; 
import TeamEdit from './pages/Teams/TeamEdit';

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ===== Redirect mặc định ===== */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* ===== AUTH ===== */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Layout Chính (Sidebar + Navbar sẽ hiện ở đây) */}
        <Route element={<MainLayout />}>
            
            {/* 👇 Route của User */}
            <Route path="/users" element={<UserManagement />} />
            <Route path="/profile" element={<UserProfile />} />
            
            {/* 👇 Route Workspace & Education */}
            <Route path="/workspace" element={<TaskBoard />} />
            <Route path="/milestones" element={<MilestonePage />} />
            <Route path="/ai-planning" element={<AiPlanning />} />
            <Route path="/classes" element={<ClassManager />} />
            <Route path="/subjects" element={<SubjectManager />} />

            {/* 👇 Route team */}
            <Route path="/resources/*" element={<ResourcePage />} />
            <Route path="/collaborations/*" element={<CollaborationPage />} />
            <Route path="/teams" element={<TeamList />} />
            <Route path="/teams/new" element={<TeamCreate />} />
            <Route path="/teams/:id" element={<TeamDetail />} />
            <Route path="/teams/:id/edit" element={<TeamEdit />} />


            {/* 👇 ROUTE RESOURCE (ĐÃ CHỈNH SỬA) */}
            {/* Truy cập http://localhost:3000/resources để vào trang quản lý file */}
            <Route path="/resources" element={<ResourcePage />} />

            {/* 👇 Route Project */}
            <Route path="/projects" element={<ProjectList />} />
            <Route path="/projects/new" element={<ProjectForm />} />

        </Route>

        {/* ===== Fallback (Trang 404 hoặc về Login) ===== */}
        <Route path="*" element={<Navigate to="/login" replace />} />
        

      </Routes>
    </BrowserRouter>
  );
}

export default App;