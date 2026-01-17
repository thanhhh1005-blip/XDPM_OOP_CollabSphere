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
import ChatRoom from './ChatRoom';

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => { 
  const [openChat, setOpenChat] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  
  // Lấy thông tin user từ LocalStorage
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}'); 
  const userRole = savedUser.role; 
  console.log("User Role in MainLayout:", savedUser);

  // 1. Khai báo danh sách Menu
  const items = [
    { key: '/workspace', icon: <ProjectOutlined />, label: 'Quản lý Sprint', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
    { key: '/projects', icon: <FolderOutlined />, label: 'Dự án', roles: ['LECTURER', 'HEAD_DEPARTMENT'] },
    { key: '/teams', icon: <TeamOutlined />, label: 'Team', roles: ['LECTURER', 'STUDENT'] },
    { key: '/milestones', icon: <ReadOutlined />, label: 'Lộ trình & Cột mốc', roles: ['STUDENT', 'LECTURER'] },
    { key: '/classes', icon: <TeamOutlined />, label: 'Quản lý Lớp học', roles: ['STAFF', 'ADMIN','LECTURER'] },
    { key: '/subjects', icon: <BookOutlined />, label: 'Quản lý Môn học', roles: ['ADMIN', 'STAFF'] },
    { key: '/users', icon: <UserOutlined />, label: 'Quản lý Người dùng', roles: ['ADMIN'] },
    { key: '/profile', icon: <UserOutlined />, label: 'Hồ sơ cá nhân', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
    { key: '/resources', icon: <FolderOutlined />, label: 'Kho Tài liệu', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
  ];

  // Lọc menu theo quyền (Role)
  const filteredItems = items.filter(item => item.roles.includes(userRole));

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
          selectedKeys={[location.pathname]} 
          mode="inline"
          items={filteredItems}
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
              onClick={() => navigate("/profile")} 
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
            {/* React Router sẽ render các trang con (TaskBoard, ClassManager...) vào đây */}
            <Outlet context={[userRole]}/> 
        </Content>
      </Layout>

      {/* CỬA SỔ CHAT TRƯỢT (DRAWER) */}
      <Drawer 
        title="💬 Phòng Chat" 
        placement="right" 
        onClose={() => setOpenChat(false)} 
        open={openChat} 
        // 👇 ĐÃ SỬA: Thay width={450} bằng styles (Cách chuẩn của Antd v5)
        styles={{ wrapper: { width: 450 } }}
      >
        <ChatRoom />
      </Drawer>
    </Layout>
  );
};

export default MainLayout;