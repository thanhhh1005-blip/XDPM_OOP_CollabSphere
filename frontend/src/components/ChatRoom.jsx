import React, { useState, useEffect, useCallback, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { Input, Button, List, Card, Tag, Modal, message, Typography, Avatar, Empty, Tabs, Spin, Space } from "antd";
import { SendOutlined, VideoCameraOutlined, TeamOutlined, BankOutlined, ArrowLeftOutlined, CloseCircleOutlined } from "@ant-design/icons";
import axios from "axios";

const ChatRoom = () => {
  const [activeTab, setActiveTab] = useState('TEAM'); 
  const [activeChat, setActiveChat] = useState(null); 
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isVideoOpen, setIsVideoOpen] = useState(false);
  const [loadingHistory, setLoadingHistory] = useState(false);
  
  // Trạng thái cuộc họp
  const [activeMeeting, setActiveMeeting] = useState(null);
  const [meetingPass, setMeetingPass] = useState("123456");
  
  // Cờ chặn cập nhật sai (QUAN TRỌNG)
  const isMeetingEndedRef = useRef(false);

  const scrollRef = useRef(null);
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  // --- 1. LOAD DANH SÁCH ---
  const [chatList, setChatList] = useState({ teams: [], classes: [] });

  const loadData = async () => {
    try {
      const url = user.role === 'LECTURER' 
        ? `http://localhost:8080/api/v1/teams/lecturer/${user.username}`
        : `http://localhost:8080/api/v1/teams/student/${user.username}`;
      
      const res = await axios.get(url);
      const teamsRaw = res.data?.result || res.data || [];
      const classRes = await axios.get(`http://localhost:8080/api/classes`);
      const allClasses = classRes.data?.result || classRes.data || [];

      setChatList({
        teams: teamsRaw.map(t => {
            const matchClass = allClasses.find(c => String(c.id) === String(t.classId));
            return { id: t.id, name: t.name, classCode: matchClass?.code || 'N/A', type: 'TEAM' };
        }),
        classes: Array.from(new Set(teamsRaw.map(t => t.classId))).map(id => {
            const matchClass = allClasses.find(c => String(c.id) === String(id));
            return { id: id, name: matchClass ? `Lớp ${matchClass.code}` : `Lớp học ${id}`, type: 'CLASS' };
        })
      });
    } catch (e) {}
  };

  useEffect(() => { loadData(); }, [user.username]);

  // --- 2. HÀM KIỂM TRA TRẠNG THÁI (CÓ CHẶN CỜ) ---
  const checkStatus = useCallback(async (roomId) => {
    if (!roomId || isMeetingEndedRef.current) return; // Nếu đã kết thúc thì không check nữa

    try {
      const res = await axios.get(`http://localhost:8080/api/chat/meetings/${roomId}/status`);
      const data = res.data?.result || res.data;
      if (!isMeetingEndedRef.current) {
          setActiveMeeting(data || null);
      }
    } catch (e) { setActiveMeeting(null); }
  }, []);

  // --- 3. KẾT NỐI SOCKET ---
  useEffect(() => {
    if (!activeChat) return;
    const roomId = `${activeChat.type}_${activeChat.id}`;
    
    // Reset khi vào phòng mới
    setActiveMeeting(null);
    setIsVideoOpen(false);
    isMeetingEndedRef.current = false; // Reset cờ

    setLoadingHistory(true);
    axios.get(`http://localhost:8080/api/chat/history/${roomId}`)
         .then(res => setMessages(res.data?.result || res.data || []))
         .finally(() => setLoadingHistory(false));

    checkStatus(roomId);

    const client = new Client({
      brokerURL: "ws://localhost:8080/ws",
      onConnect: () => {
        setIsConnected(true);
        client.subscribe(`/topic/room/${roomId}`, (msgData) => {
          const msg = JSON.parse(msgData.body);
          
          if (msg.type === 'CHAT') {
            setMessages((prev) => [...prev, msg]);
          } 
          else if (msg.type === 'CALL_END') {
             // ⛔ CHẶN ĐỨNG MỌI CẬP NHẬT
             isMeetingEndedRef.current = true;
             setIsVideoOpen(false);
             setActiveMeeting(null);
             message.info("Giảng viên đã giải tán lớp học.");
          }
          else if (msg.type === 'CALL_START') {
             isMeetingEndedRef.current = false;
             checkStatus(roomId);
          }
        });
      },
      onDisconnect: () => setIsConnected(false)
    });

    client.activate();
    setStompClient(client);

    const timer = setInterval(() => checkStatus(roomId), 3000); 
    return () => { client.deactivate(); clearInterval(timer); };
  }, [activeChat]);

  useEffect(() => {
    if (scrollRef.current) scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
  }, [messages]);

  // --- CÁC HÀM XỬ LÝ ---
  const handleStartMeeting = async () => {
    const roomId = `${activeChat.type}_${activeChat.id}`;
    try {
      isMeetingEndedRef.current = false;
      await axios.post(`http://localhost:8080/api/chat/meetings/${roomId}/start?hostName=${user.fullName}&password=${meetingPass}`);
      message.success("Đã mở phòng họp!");
      checkStatus(roomId);
      setIsVideoOpen(true);
    } catch (e) { message.error("Lỗi mở họp"); }
  };

  const handleEndMeeting = async () => {
    if(!confirm("Bạn có chắc muốn giải tán lớp?")) return;
    const roomId = `${activeChat.type}_${activeChat.id}`;
    
    // Đặt cờ ngay lập tức để UI không bị nháy lại
    isMeetingEndedRef.current = true;
    setActiveMeeting(null);
    setIsVideoOpen(false);

    try {
        await axios.delete(`http://localhost:8080/api/chat/meetings/${roomId}/end`);
        
        // Gửi tín hiệu giải tán
        if (stompClient && isConnected) {
            stompClient.publish({
                destination: "/app/chat.sendMessage",
                body: JSON.stringify({
                    senderId: user.id, senderName: "HỆ THỐNG", 
                    content: "Cuộc họp đã kết thúc.", roomId: roomId, type: "CALL_END"
                }),
            });
        }
        message.success("Đã giải tán lớp học");
    } catch(e) {}
  };

  const sendMessage = () => {
    if (inputText.trim() && stompClient && isConnected) {
      const roomId = `${activeChat.type}_${activeChat.id}`;
      stompClient.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify({
          senderId: user.id, senderName: user.fullName,
          content: inputText, roomId: roomId, type: "CHAT",
        }),
      });
      setInputText("");
    }
  };

  // ... (Phần renderListView và renderChatView giữ nguyên như bản cũ vì không có lỗi) ...
  // Để tiết kiệm không gian chat, em dùng lại phần render của bản v6.0 nhé, logic ở trên mới quan trọng.
  // Nếu em cần full file thì bảo thầy gửi lại, nhưng chủ yếu sửa đoạn useEffect và handleEndMeeting ở trên thôi.

  const renderListView = () => (
    <div style={{ height: '100%', background: '#fff' }}>
      <Tabs activeKey={activeTab} onChange={setActiveTab} centered items={[
        { key: 'TEAM', label: <span><TeamOutlined /> Nhóm</span> },
        { key: 'CLASS', label: <span><BankOutlined /> Lớp học</span> },
      ]} />
      <List style={{ padding: '0 10px' }} dataSource={activeTab === 'TEAM' ? chatList.teams : chatList.classes}
        renderItem={item => (
          <List.Item style={{ cursor: 'pointer', padding: '15px', borderRadius: '12px', marginBottom: '8px', background: '#f8f9fa' }} onClick={() => setActiveChat(item)}>
            <List.Item.Meta avatar={<Avatar icon={item.type === 'TEAM' ? <TeamOutlined /> : <BankOutlined />} style={{backgroundColor: item.type === 'TEAM' ? '#1890ff' : '#52c41a'}} />} 
              title={<b>{item.name}</b>} description={item.type === 'TEAM' ? <Tag color="blue">{item.classCode}</Tag> : "Phòng chat lớp"} />
          </List.Item>
        )}
      />
    </div>
  );

  const renderChatView = () => (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: '#f0f2f5' }}>
      <div style={{ padding: '12px', display: 'flex', alignItems: 'center', background: '#fff', borderBottom: '1px solid #eee' }}>
        <Button icon={<ArrowLeftOutlined />} type="text" onClick={() => setActiveChat(null)} />
        <div style={{ marginLeft: 10, flex: 1 }}>
          <b style={{ display: 'block' }}>{activeChat.name}</b>
          <small style={{color: isConnected ? '#52c41a' : '#ccc'}}>● {isConnected ? 'Trực tuyến' : 'Kết nối...'}</small>
        </div>

        <Space>
            {user.role === 'LECTURER' ? (
                !activeMeeting ? (
                    <>
                        <Button type="primary" danger size="small" icon={<VideoCameraOutlined />} onClick={handleStartMeeting}>Mở Họp</Button>
                    </>
                ) : (
                    <>
                        <Button type="primary" style={{background:'#faad14', border:'none'}} size="small" onClick={() => setIsVideoOpen(true)}>Vào lại</Button>
                        <Button type="primary" danger size="small" icon={<CloseCircleOutlined />} onClick={handleEndMeeting} title="Giải tán lớp" />
                    </>
                )
            ) : (
                <Button 
                    type="primary" shape="round" icon={<VideoCameraOutlined />} 
                    style={{ background: activeMeeting ? '#52c41a' : '#bfbfbf', border: 'none' }}
                    disabled={!activeMeeting}
                    onClick={() => setIsVideoOpen(true)}
                >
                    {activeMeeting ? 'Tham gia' : 'Offline'}
                </Button>
            )}
        </Space>
      </div>

      <div ref={scrollRef} style={{ flex: 1, overflowY: 'auto', padding: '15px' }}>
          {loadingHistory && <div style={{textAlign:'center', padding: 10}}><Spin size="small" /> Đang tải...</div>}
          
          {activeMeeting && (
            <div style={{ textAlign: 'center', marginBottom: 20 }}>
                <Tag color="processing" style={{cursor:'pointer'}} onClick={() => setIsVideoOpen(true)}>
                    🎥 Cuộc họp đang diễn ra. Bấm tham gia!
                </Tag>
            </div>
          )}

          <List dataSource={messages} renderItem={msg => (
               (msg.type === 'CHAT') ? (
              <div style={{ textAlign: msg.senderId === user.id ? 'right' : 'left', marginBottom: 15 }}>
                  <div style={{ fontSize: '10px', color: '#888', marginBottom: '2px' }}>{msg.senderName}</div>
                  <div style={{ display: 'inline-block', padding: '8px 12px', borderRadius: '15px', background: msg.senderId === user.id ? '#1890ff' : '#fff', color: msg.senderId === user.id ? '#fff' : '#000', boxShadow: '0 1px 2px rgba(0,0,0,0.1)', maxWidth: '85%', textAlign: 'left' }}>{msg.content}</div>
              </div>
               ) : (
                <div style={{textAlign:'center', margin:'10px 0', fontSize:'12px', color:'#999'}}>
                    <i>{msg.content}</i>
                </div>
              )
          )} />
      </div>

      <div style={{ padding: '15px', background: '#fff' }}>
        <Input placeholder="Nhắn tin..." value={inputText} onChange={e => setInputText(e.target.value)} onPressEnter={sendMessage}
          suffix={<SendOutlined style={{ color: '#1890ff', cursor: 'pointer' }} onClick={sendMessage} />} />
      </div>
    </div>
  );

  return (
    <div style={{ height: '100%' }}>
      {activeChat ? renderChatView() : renderListView()}
      <Modal open={isVideoOpen} width={1000} onCancel={() => setIsVideoOpen(false)} footer={null} destroyOnClose maskClosable={false}>
        <div style={{height: 600}}>
           <iframe src={`https://meet.ffmuc.net/CollabSphere_${activeChat?.type}_${activeChat?.id}#userInfo.displayName="${user.fullName}"`}
                style={{width: '100%', height: '100%', border: 'none'}}
                allow="camera; microphone; display-capture; fullscreen"></iframe>
        </div>
      </Modal>
    </div>
  );
};
export default ChatRoom;