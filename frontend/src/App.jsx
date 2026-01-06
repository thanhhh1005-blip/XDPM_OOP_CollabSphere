import React from 'react';
import { Layout, Typography } from 'antd';
  import ChatRoom from './components/Chatroom.jsx';
import TaskBoard from './pages/Workspace/TaskBoard';

const { Header, Content, Sider } = Layout;
const { Title } = Typography;

function App() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center' }}>
        <Title level={3} style={{ color: 'white', margin: 0 }}>🚀 CollabSphere Demo</Title>
      </Header>
      <Layout>
        {/* Phần nội dung chính: Kanban Board */}
        <Content style={{ padding: '24px', backgroundColor: '#fff' }}>
          <TaskBoard />
        </Content>
        
        {/* Phần bên phải: Chat Bar */}
        <Sider width={400} theme="light" style={{ borderLeft: '1px solid #f0f0f0' }}>
          <ChatRoom />
        </Sider>
      </Layout>
    </Layout>
  );
}

export default App;