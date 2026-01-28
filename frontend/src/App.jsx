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
import ClassSchedule from './pages/Education/ClassSchedule';
// Các trang khác
import AiPlanning from './pages/AI/AiPlanning';
import WorkspaceList from './pages/Workspace/WorkspaceList';
import TaskBoard from './pages/Workspace/TaskBoard';
import MilestonePage from './pages/Workspace/MilestonePage';
import SubjectManager from './pages/Education/SubjectManager';
import ClassManager from './pages/Education/ClassManager';
import ProjectList from './pages/Projects/ProjectList'; 
import ProjectForm from './pages/Projects/ProjectForm';
import TeamList from './pages/Teams/TeamList';
import TeamCreate from './pages/Teams/TeamCreate';
import TeamDetail from './pages/Teams/TeamDetail';
import CollabPage from './pages/Collaboration/CollabPage';
import CollabList from './pages/Collaboration/CollabList';
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

        <Route element={<MainLayout />}>
            <Route path="/users" element={<UserManagement />} />
            <Route path="/profile" element={<UserProfile />} />
            <Route path="/workspace" element={<WorkspaceList />} />
            <Route path="/workspace/:id" element={<TaskBoard />} />
            <Route path="/workspace/:id/class/:classId/board" element={<TaskBoard />} />
            
            <Route path="/collaboration" element={<CollabList />} />
            <Route path="/collaboration/:roomId" element={<CollabPage />} />
            <Route path="milestones" element={<MilestonePage />} />          
            <Route path="milestones/:classId" element={<MilestonePage />} />   
            <Route path="/ai-planning" element={<AiPlanning />} />
            <Route path="/classes" element={<ClassManager />} />
            <Route path="/subjects" element={<SubjectManager />} />

            <Route path="/resources" element={<ResourcePage />} />
            <Route path="/projects" element={<ProjectList />} />  
            <Route path="/projects/new" element={<ProjectForm />} /> 
            <Route path="/teams" element={<TeamList />} />
            <Route path="/teams/new" element={<TeamCreate />} />
            <Route path="/teams/:id" element={<TeamDetail />} />
            <Route path="/teams/:id/edit" element={<TeamEdit />} />
            <Route path="/schedule/:classId" element={<ClassSchedule />} />
            <Route path="/schedule" element={<ClassSchedule />} />
            <Route path="/education/class/:classId/schedule" element={<ClassSchedule />} />
        </Route>

        {/* ===== Fallback (Trang 404 hoặc về Login) ===== */}
        <Route path="*" element={<Navigate to="/login" replace />} />
        

      </Routes>
    </BrowserRouter>
  );
}

export default App;