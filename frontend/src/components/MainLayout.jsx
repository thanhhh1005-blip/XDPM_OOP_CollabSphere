import React, { useState, useEffect } from 'react';
import { Layout, Menu, Button, Drawer, Typography, Avatar } from 'antd';
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

/* ===== COMPONENT CŨ CỦA NGƯỜI KHÁC (GIỮ NGUYÊN) ===== */
import TaskBoard from '../pages/Workspace/TaskBoard';
import AiPlanning from '../pages/AI/AiPlanning';
import ClassManager from '../pages/Education/ClassManager';
import SubjectManager from '../pages/Education/SubjectManager';

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => {
  const [openChat, setOpenChat] = useState(false);
  const [selectedKey, setSelectedKey] = useState('1');

  const navigate = useNavigate();
  const location = useLocation();

  /* ====================================================
     1. SYNC URL ↔ MENU
     [THÊM] resource
  ==================================================== */
  useEffect(() => {
    const path = location.pathname;
    if (path.includes('/ai-planning')) setSelectedKey('2');
    else if (path.includes('/classes')) setSelectedKey('3');
    else if (path.includes('/subjects')) setSelectedKey('4');
    else if (path.includes('/users')) setSelectedKey('5');
    else if (path.includes('/profile')) setSelectedKey('6');
    else if (path.includes('/resources')) setSelectedKey('7'); 
    else setSelectedKey('1');
  }, [location]);

  /* ====================================================
     2. MENU ITEMS
     [THÊM] Resource
  ==================================================== */
  const items = [
    { key: '1', icon: <ProjectOutlined />, label: 'Quản lý Sprint' },
    { key: '2', icon: <BulbOutlined />, label: 'AI Lên Ý Tưởng' },
    { key: '3', icon: <ReadOutlined />, label: 'Class Management' },
    { key: '4', icon: <BookOutlined />, label: 'Subject Management' },
    { key: '5', icon: <TeamOutlined />, label: 'Quản lý User' },
    { key: '6', icon: <UserOutlined />, label: 'Hồ sơ cá nhân' },
    { key: '7', icon: <FolderOutlined />, label: 'Resource' },
  ];

  /* ====================================================
     3. HANDLE MENU CLICK
     [THÊM] navigate resource
  ==================================================== */
  const handleMenuClick = (e) => {
    setSelectedKey(e.key);
    switch (e.key) {
      case '1': navigate('/workspace'); break;
      case '2': navigate('/ai-planning'); break;
      case '3': navigate('/classes'); break;
      case '4': navigate('/subjects'); break;
      case '5': navigate('/users'); break;
      case '6': navigate('/profile'); break;
      case '7': navigate('/resources'); break;
      default: navigate('/workspace');
    }
  };

  /* ====================================================
     4. RENDER CONTENT
     [THÊM] Resource dùng Outlet
  ==================================================== */
  const renderContent = () => {
    switch (selectedKey) {
      /* ===== LOGIC CŨ ===== */
      case '1': return <TaskBoard />;
      case '2': return <AiPlanning />;
      case '3': return <ClassManager />;
      case '4': return <SubjectManager />;
      case '5':
      case '6':
      case '7':
        return <Outlet />;
      default:
        return <TaskBoard />;
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
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
          selectedKeys={[selectedKey]}
          mode="inline"
          items={items}
          onClick={handleMenuClick}
        />
      </Sider>

      <Layout>
        <Header
          style={{
            padding: '0 20px',
            background: '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            boxShadow: '0 1px 4px rgba(0,21,41,0.08)'
          }}
        >
          <Title level={4} style={{ margin: 0 }}>Dashboard</Title>
          <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
            <Avatar
              icon={<UserOutlined />}
              style={{ cursor: 'pointer' }}
              onClick={() => navigate('/profile')}
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

        <Content
          style={{
            margin: '16px',
            padding: 24,
            background: '#fff',
            borderRadius: 8,
            overflowY: 'auto'
          }}
        >
          {renderContent()}
        </Content>
      </Layout>

      <Drawer
        title="💬 Phòng Chat"
        placement="right"
        onClose={() => setOpenChat(false)}
        open={openChat}
        width={450}
      >
        <ChatRoom />
      </Drawer>
    </Layout>
  );
};

export default MainLayout;
