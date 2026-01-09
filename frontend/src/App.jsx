import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";
import UserManagement from './pages/User/UserManagement';
import UserProfile from './pages/User/UserProfile';


function App() {
  return (
    <Router>
      <Routes>
        {/* 1. Login/Register */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* 2. Main Layout */}
        <Route path="/" element={<MainLayout />}>
          {/* - Khi vào "/" -> MainLayout load -> useEffect set Key='1' -> renderContent hiện TaskBoard (Logic cũ).
             - Khi vào "/users" -> MainLayout load -> useEffect set Key='3' -> renderContent hiện Outlet -> Outlet hiện UserManagement (Logic mới).
          */}
          <Route path="users" element={<UserManagement />} />
          <Route path="profile" element={<UserProfile />} />
          
          {/* Lưu ý: Route ai-planning vẫn để đây cho đúng chuẩn Router, 
              dù MainLayout đang import cứng AiPlanning thì cũng không sao, 
              nó sẽ ưu tiên logic trong renderContent của MainLayout */}
          <Route path="ai-planning" element={<div></div>} /> 
        </Route>

        {/* 3. 404 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;