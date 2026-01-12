<<<<<<< HEAD
import React, { useState } from 'react';
import { Layout, Menu, Button, Drawer, Typography, Avatar } from 'antd';
import { ProjectOutlined, BulbOutlined, MessageOutlined, UserOutlined } from '@ant-design/icons';
import ChatRoom from './ChatRoom'; // Import Chat
import TaskBoard from '../pages/Workspace/TaskBoard'; // Import Bảng Task của em (lưu ý đường dẫn nếu em để trong pages)
import AiPlanning from '../pages/AI/AiPlanning'; // Import trang AI mới tạo
import EvaluationPage from '../pages/Evaluation/EvaluationPage';
import NotificationPage from "../pages/Notification/NotificationPage";
import NotificationBell from "./NotificationBell";
=======
import React, { useState } from "react";
import { Layout, Menu, Button, Drawer, Typography, Avatar, Badge, Tag } from 'antd';
import {
  ProjectOutlined,
  BulbOutlined,
  MessageOutlined,
  UserOutlined,
  ReadOutlined,
  BookOutlined,
  TeamOutlined,
  FolderOutlined   //  Resource
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Select } from 'antd';
import ChatRoom from './ChatRoom';

/* ===== COMPONENT CŨ CỦA NGƯỜI KHÁC (GIỮ NGUYÊN) ===== */
import TaskBoard from '../pages/Workspace/TaskBoard';
import AiPlanning from '../pages/AI/AiPlanning';
import ClassManager from '../pages/Education/ClassManager';
import SubjectManager from '../pages/Education/SubjectManager';
import ProjectList from '../pages/Projects/ProjectList';
>>>>>>> cb0127ccb26ef039532b05bccc7c276fa2554861

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => { 
  const [openChat, setOpenChat] = useState(false);

  const [selectedKey, setSelectedKey] = useState('1');

  const navigate = useNavigate();
  const location = useLocation();
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}'); // Vai trò người dùng hiện tại
  const userRole = savedUser.role; 
  console.log("User Role in MainLayout:", savedUser);

  // 1. Khai báo danh sách Menu
  // QUAN TRỌNG: 'key' phải trùng khớp với 'path' em đã đặt trong App.jsx
  const items = [
<<<<<<< HEAD
    { key: '1', icon: <ProjectOutlined />, label: 'Quản lý Sprint' },
    { key: '2', icon: <BulbOutlined />, label: 'AI Lên Ý Tưởng' },
    { key: '3', icon: <UserOutlined />, label: 'Evaluation' },
    { key: '4', icon: <div style={{ fontSize: 18 }}>🔔</div>, label: 'Thông báo' },
  ];

  const renderContent = () => {
    switch (selectedKey) {
        case '1': return <TaskBoard />; // Của em
        case '2': return <AiPlanning />; // Của bạn em
        case '3': return <EvaluationPage />;
        case '4': return <NotificationPage userId={currentUserId} />;
        default: return <div>Chọn menu để bắt đầu</div>;
    }
  };

=======
    { key: '/workspace', icon: <ProjectOutlined />, label: 'Quản lý Sprint', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
    { key: '/projects', icon: <FolderOutlined />, label: 'Dự án', roles: ['LECTURER', 'HEAD_DEPARTMENT'] },
    { key: '/teams', icon: <TeamOutlined />, label: 'Team', roles: ['LECTURER', 'STUDENT'] },
    { key: '/milestones', icon: <ReadOutlined />, label: 'Lộ trình & Cột mốc', roles: ['STUDENT', 'LECTURER'] },
    { key: '/classes', icon: <TeamOutlined />, label: 'Quản lý Lớp học', roles: ['LECTURER', 'ADMIN'] },
    { key: '/subjects', icon: <BookOutlined />, label: 'Quản lý Môn học', roles: ['ADMIN'] },
    { key: '/users', icon: <UserOutlined />, label: 'Quản lý Người dùng', roles: ['ADMIN'] },
    { key: '/profile', icon: <UserOutlined />, label: 'Hồ sơ cá nhân', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
  ];
  const filteredItems = items.filter(item => item.roles.includes(userRole));
>>>>>>> cb0127ccb26ef039532b05bccc7c276fa2554861
  return (
    <Layout style={{ minHeight: "100vh" }}>
      {/* SIDEBAR BÊN TRÁI */}
      <Sider theme="light" width={250}>

        <div
          style={{
            height: 50,
            margin: 16,
            background: '#001529',
            borderRadius: 6,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontWeight: 'bold',
            fontSize: '18px'
          }}
        >
          CollabSphere
        </div>
        <Menu
          theme="light"
          // Tự động sáng menu dựa trên URL hiện tại (Ví dụ đang ở /workspace thì menu 1 sáng)
          selectedKeys={[location.pathname]} 
          mode="inline"
          items={filteredItems}
          // Khi bấm vào menu, nó nhảy thẳng tới URL đó
          onClick={(e) => navigate(e.key)} 
        />
      </Sider>

      <Layout>
        {/* HEADER Ở TRÊN */}
        <Header style={{ padding: "0 20px", background: "#fff", display: "flex", justifyContent: "space-between", alignItems: "center", boxShadow: "0 1px 4px rgba(0,21,41,0.08)" }}>
          <div>
            <Title level={4} style={{ margin: 0 }}>Dashboard</Title>
          </div>
          
          <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
            <div style={{ lineHeight: '1.2' }}>
                <div style={{ fontWeight: 'bold' }}>{savedUser.fullName}</div>
                <Tag color="blue">{userRole}</Tag> 
            </div>
            <Avatar
              icon={<UserOutlined />}
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/profile")} // Bấm avatar nhảy về trang cá nhân
            />
            <Button
              type="primary"
              shape="round"
              icon={<MessageOutlined />}
              onClick={() => setOpenChat(true)}
            >
              Chat Nhóm
            </Button>
          </div>
        </Header>

        {/* NỘI DUNG CHÍNH Ở GIỮA */}
        <Content style={{ margin: "16px", padding: 24, background: "#fff", borderRadius: 8, overflowY: "auto" }}>
            
            {/* 👇 ĐÂY LÀ CHỖ THAY THẾ CHO renderContent() 👇 */}
            {/* React Router sẽ tự động lấy TaskBoard, AiPlanning... đặt vào đây dựa trên URL */}
            <Outlet context={[userRole]}/> 

        </Content>
      </Layout>

      {/* CỬA SỔ CHAT TRƯỢT (DRAWER) */}
      <Drawer title="💬 Phòng Chat" placement="right" onClose={() => setOpenChat(false)} open={openChat} width={450}>
        <ChatRoom />
      </Drawer>
    </Layout>
  );
};

export default MainLayout;
