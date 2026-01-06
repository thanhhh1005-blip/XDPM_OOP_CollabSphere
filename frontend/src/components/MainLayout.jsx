import React, { useState } from 'react';
import { Layout, Menu, Button, Drawer, Typography, Avatar } from 'antd';
import { ProjectOutlined, BulbOutlined, MessageOutlined, UserOutlined } from '@ant-design/icons';
import ChatRoom from './ChatRoom'; // Import Chat
import TaskBoard from '../pages/Workspace/TaskBoard'; // Import Bảng Task của em (lưu ý đường dẫn nếu em để trong pages)
import AiPlanning from '../pages/AI/AiPlanning'; // Import trang AI mới tạo

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const MainLayout = () => {
  const [openChat, setOpenChat] = useState(false);
  const [selectedKey, setSelectedKey] = useState('1'); 

  // Menu bên trái
  const items = [
    { key: '1', icon: <ProjectOutlined />, label: 'Quản lý Sprint' },
    { key: '2', icon: <BulbOutlined />, label: 'AI Lên Ý Tưởng' },
  ];

  const renderContent = () => {
    switch (selectedKey) {
        case '1': return <TaskBoard />; // Của em
        case '2': return <AiPlanning />; // Của bạn em
        default: return <div>Chọn menu để bắt đầu</div>;
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={250}>
        <div style={{ height: 50, margin: 16, background: '#001529', borderRadius: 6, display:'flex', alignItems:'center', justifyContent:'center', color:'white', fontWeight:'bold', fontSize:'18px' }}>
            CollabSphere
        </div>
        <Menu theme="light" defaultSelectedKeys={['1']} mode="inline" items={items} onClick={(e) => setSelectedKey(e.key)} />
      </Sider>

      <Layout>
        <Header style={{ padding: '0 20px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 4px rgba(0,21,41,0.08)' }}>
            <Title level={4} style={{ margin: 0 }}>Dashboard</Title>
            <Button type="primary" shape="round" icon={<MessageOutlined />} onClick={() => setOpenChat(true)}>Chat Nhóm</Button>
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