import React, { useState, useEffect } from 'react';
import { Layout, Menu, Button, Drawer, Typography, Avatar } from 'antd';
// 👇 Thêm Outlet, useNavigate, useLocation
import { Outlet, useNavigate, useLocation } from 'react-router-dom'; 
import { ProjectOutlined, BulbOutlined, MessageOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons';
import ChatRoom from './ChatRoom'; 
import TaskBoard from '../pages/Workspace/TaskBoard'; 
import AiPlanning from '../pages/AI/AiPlanning'; 

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => {
  const [openChat, setOpenChat] = useState(false);
  const [selectedKey, setSelectedKey] = useState('1'); 
  
  // 👇 Hook điều hướng
  const navigate = useNavigate();
  const location = useLocation();

  // 👇 Logic mới: Đồng bộ URL với Menu (Giữ trạng thái khi F5)
  useEffect(() => {
    const path = location.pathname;
    if (path.includes('/ai-planning')) setSelectedKey('2');
    else if (path.includes('/users')) setSelectedKey('3'); // Key mới
    else if (path.includes('/profile')) setSelectedKey('4'); // Key mới
    else setSelectedKey('1'); // Mặc định về Workspace
  }, [location]);

  // Menu bên trái
  const items = [
    { key: '1', icon: <ProjectOutlined />, label: 'Quản lý Sprint' },
    { key: '2', icon: <BulbOutlined />, label: 'AI Lên Ý Tưởng' },
    // 👇 Thêm menu mới của bạn
    { key: '3', icon: <TeamOutlined />, label: 'Quản lý User' },
    { key: '4', icon: <UserOutlined />, label: 'Hồ sơ cá nhân' },
  ];

  // 👇 Xử lý khi click menu: Vừa setKey vừa chuyển trang
  const handleMenuClick = (e) => {
      setSelectedKey(e.key);
      if (e.key === '1') navigate('/'); // Hoặc /workspace tùy route bạn
      if (e.key === '2') navigate('/ai-planning');
      if (e.key === '3') navigate('/users');
      if (e.key === '4') navigate('/profile');
  };

  const renderContent = () => {
    switch (selectedKey) {
        case '1': return <TaskBoard />; // ✅ Logic cũ giữ nguyên
        case '2': return <AiPlanning />; // ✅ Logic cũ giữ nguyên
        // 👇 Logic mới: Nếu là key 3, 4 hoặc khác -> Trả về Outlet để Router xử lý
        default: return <Outlet />; 
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={250}>
        <div style={{ height: 50, margin: 16, background: '#001529', borderRadius: 6, display:'flex', alignItems:'center', justifyContent:'center', color:'white', fontWeight:'bold', fontSize:'18px' }}>
            CollabSphere
        </div>
        {/* Sửa onClick thành handleMenuClick */}
        <Menu theme="light" selectedKeys={[selectedKey]} mode="inline" items={items} onClick={handleMenuClick} />
      </Sider>

      <Layout>
        <Header style={{ padding: '0 20px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 4px rgba(0,21,41,0.08)' }}>
            
            <div style={{display: 'flex', gap: '10px', alignItems: 'center'}}>
                 {/* Thêm nút Profile nhanh ở Header nếu thích */}
                 <Avatar icon={<UserOutlined />} style={{cursor: 'pointer'}} onClick={() => navigate('/profile')} />
                 <Button type="primary" shape="round" icon={<MessageOutlined />} onClick={() => setOpenChat(true)}>Chat Nhóm</Button>
            </div>
        </Header>

        <Content style={{ margin: '16px', padding: 24, background: '#fff', borderRadius: 8, overflowY: 'auto' }}>
            {renderContent()}
        </Content>
      </Layout>

      {/* Chat Drawer */}
      <Drawer title="💬 Phòng Chat" placement="right" onClose={() => setOpenChat(false)} open={openChat} width={450}>
        <ChatRoom />
      </Drawer>
    </Layout>
  );
};
export default MainLayout;