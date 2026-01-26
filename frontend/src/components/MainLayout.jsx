import React, { useState, useEffect } from "react";
import { Layout, Menu, Button, Drawer, Typography, Avatar, Badge, Tag } from 'antd';
import {
  ProjectOutlined,
  BulbOutlined,
  MessageOutlined,
  UserOutlined,
  ReadOutlined,
  BookOutlined,
  TeamOutlined,
  FolderOutlined,   //  Resource
  EditOutlined,
  LogoutOutlined // <--- 1. THÊM ICON ĐĂNG XUẤT
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import ChatRoom from './ChatRoom';
import axios from "axios";

/* ===== COMPONENT CŨ CỦA NGƯỜI KHÁC (GIỮ NGUYÊN) ===== */
import TaskBoard from '../pages/Workspace/TaskBoard';
import AiPlanning from '../pages/AI/AiPlanning';
import ClassManager from '../pages/Education/ClassManager';
import SubjectManager from '../pages/Education/SubjectManager';
import ProjectList from '../pages/Projects/ProjectList';

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => {
  const [openChat, setOpenChat] = useState(false);

  const [selectedKey, setSelectedKey] = useState('1');

  const navigate = useNavigate();
  const location = useLocation();
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}');
  const userRole = savedUser.role;
  console.log("User Role in MainLayout:", savedUser);
  const groupId = savedUser.teamId;


  // 1. Thêm useEffect để tự động nạp TeamId và ClassId nếu thiếu
  useEffect(() => {
    const enrichUserInfo = async () => {
      // Nếu là Sinh viên mà trong máy chưa có teamId
      if (userRole === 'STUDENT' && savedUser.username && !savedUser.teamId) {
        try {
          
          console.log(">>> Đang đi tìm 'hộ khẩu' cho SV:", savedUser.username);
          // Gọi API team-service để lấy nhóm của sinh viên này
          const res = await axios.get(`http://localhost:8080/api/v1/teams/student/${savedUser.username}`);
          const myTeams = res.data?.result || res.data || [];
          if (myTeams.length > 0) {
            const myTeam = myTeams[0]; // Lấy nhóm đầu tiên
            const newUser = { 
              ...savedUser, 
              teamId: myTeam.id, 
              classId: myTeam.classId 
            };
            // Lưu lại vào máy cục User đầy đủ
            localStorage.setItem('user', JSON.stringify(newUser));
            console.log(">>> Đã cập nhật xong ID Nhóm và Lớp cho SV!");
            
            // Ép trang web load lại 1 lần để các linh kiện khác nhận dữ liệu mới
            window.location.reload(); 
          }
        } catch (e) {
          console.error("Lỗi khi đi tìm nhóm cho sinh viên:", e);
        }
      }
    };
    enrichUserInfo();
  }, [userRole, savedUser.username]);
  // --- 2. THÊM HÀM XỬ LÝ ĐĂNG XUẤT ---
  const handleLogout = () => {
    // Xóa thông tin user đã lưu
    localStorage.removeItem('user');
    // Chuyển hướng về trang login
    navigate('/login');
  };
  // ------------------------------------

  const items = [
    { key: '/workspace', icon: <ProjectOutlined />, label: 'Quản lý Sprint', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
    { key: '/collaboration', icon: <EditOutlined />, label: 'Cộng tác nhóm', roles: ['STUDENT', 'LECTURER'],onlyIfHasGroup: true},
    { key: '/projects', icon: <FolderOutlined />, label: 'Dự án', roles: ['LECTURER', 'HEAD_DEPARTMENT'] },
    { key: '/teams', icon: <TeamOutlined />, label: 'Team', roles: ['LECTURER', 'STUDENT'] },
    { key: '/milestones', icon: <ReadOutlined />, label: 'Lộ trình & Cột mốc', roles: ['STUDENT', 'LECTURER'] },
    { key: '/classes', icon: <TeamOutlined />, label: 'Quản lý Lớp học', roles: ['STAFF', 'ADMIN','LECTURER'] },
    {key: '/ai-planning', icon: <BulbOutlined />, label: 'AI Lập kế hoạch', roles: ['LECTURER'] },
    { key: '/subjects', icon: <BookOutlined />, label: 'Quản lý Môn học', roles: ['ADMIN', 'STAFF'] },
    { key: '/users', icon: <UserOutlined />, label: 'Quản lý Người dùng', roles: ['ADMIN'] },
    { key: '/profile', icon: <UserOutlined />, label: 'Hồ sơ cá nhân', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
    { key: '/resources', icon: <FolderOutlined />, label: 'Kho Tài liệu', roles: ['STUDENT', 'LECTURER', 'ADMIN'] },
  ];
  const filteredItems = items.filter(item => {
    const hasRole = item.roles.includes(userRole);
    
    // Nếu là mục cần Group và user là STUDENT, phải có groupId mới hiện
    if (item.onlyIfHasGroup && userRole === 'STUDENT') {
        return hasRole && groupId; 
    }
    
    return hasRole;
  });
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
          items={filteredItems.map(({ onlyIfHasGroup, roles, ...rest }) => rest)}
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

            {/* --- 3. THÊM NÚT ĐĂNG XUẤT TẠI ĐÂY --- */}
            <Button
                danger
                type="text"
                icon={<LogoutOutlined />}
                onClick={handleLogout}
            >
                Đăng xuất
            </Button>
             {/* ------------------------------------ */}

          </div>
        </Header>

        {/* NỘI DUNG CHÍNH Ở GIỮA */}
        <Content style={{ margin: "16px", padding: 24, background: "#fff", borderRadius: 8, overflowY: "auto" }}>

          <Outlet context={[userRole]} />
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