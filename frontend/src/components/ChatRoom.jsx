import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import { Input, Button, List, Card, Tag, Modal, message } from "antd";
import { SendOutlined, VideoCameraOutlined } from "@ant-design/icons";
import VideoCall from "./VideoCall";
import axios from "axios"; 

const ChatRoom = () => {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isVideoOpen, setIsVideoOpen] = useState(false);
  const [meetingPass, setMeetingPass] = useState("");


  // --- LẤY ĐỊNH DANH THẬT TỪ IDENTITY SERVICE ---
  const [activeMeeting, setActiveMeeting] = useState(null); // Thông tin cuộc họp hiện tại
  const rawUser = localStorage.getItem('user');
  const user = rawUser ? JSON.parse(rawUser) : { fullName: "Người dùng ẩn danh", id: 0 };
  const roomId = 1;

 useEffect(() => {
    checkMeetingStatus();
    
    // Thiết lập kết nối WebSocket (giữ nguyên code cũ của em)
    const client = new Client({
      brokerURL: "ws://localhost:8080/ws",
      onConnect: () => {
        setIsConnected(true);
        client.subscribe(`/topic/room/${roomId}`, (message) => {
          const msg = JSON.parse(message.body);
          setMessages((prev) => [...prev, msg]);
        });
      },
      onDisconnect: () => setIsConnected(false),
    });
    client.activate();
    setStompClient(client);
    return () => client.deactivate();
  }, []);
  const checkMeetingStatus = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/chat/meetings/${roomId}/status`);
      setActiveMeeting(res.data.data);
    } catch (e) {
      console.error("Không thể lấy trạng thái cuộc họp");
    }
  };
  const handleStartMeeting = async () => {
  if (!meetingPass) {
    message.warning("Vui lòng đặt mật khẩu cho cuộc họp!");
    return;
  }
  try {
    // Gửi pass lên Backend
    await axios.post(`http://localhost:8080/api/chat/meetings/${roomId}/start?hostName=${user.fullName}&password=${meetingPass}`);
    await checkMeetingStatus();
    setIsVideoOpen(true);
  } catch (e) {
    message.error("Lỗi khi mở cuộc họp");
  }
};
  const handleEndMeeting = async () => {
    try {
      await axios.delete(`http://localhost:8080/api/chat/meetings/${roomId}/end`);
      setActiveMeeting(null);
      setIsVideoOpen(false);
      message.info("Cuộc họp đã kết thúc");
    } catch (e) {
      message.error("Lỗi khi kết thúc cuộc họp");
    }
  };
  const sendMessage = () => {
    if (inputText.trim() && stompClient && isConnected) {
      const chatMessage = {
        senderId: user.id,
        senderName: user.fullName,
        content: inputText,
        roomId: roomId,
        type: "CHAT",
      };
      stompClient.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify(chatMessage),
      });
      setInputText("");
    }
  };

  return (
    <Card 
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
          <span style={{ fontSize: '14px' }}>💬 Nhóm {roomId}</span>
          
          {/* --- KHU VỰC ĐIỀU KHIỂN CUỘC HỌP (VỊ TRÍ ĐÚNG Ở ĐÂY) --- */}
          <div style={{ display: 'flex', gap: '10px' }}>
            {user.role === 'LECTURER' ? (
              // 1. Nếu là GIẢNG VIÊN
              !activeMeeting ? (
                // Nếu chưa có cuộc họp -> Hiện ô nhập mật khẩu + Nút Mở
                <div style={{ display: 'flex', gap: '5px' }}>
                  <Input 
                    placeholder="Đặt mật khẩu..." 
                    size="small" 
                    style={{ width: 150 }} 
                    onChange={(e) => setMeetingPass(e.target.value)} 
                  />
                  <Button type="primary" danger size="small" onClick={handleStartMeeting}>
                    Mở cuộc họp
                  </Button>
                </div>
              ) : (
                // Nếu đã mở họp rồi -> Hiện nút Vào lại
                <Button type="primary" danger size="small" onClick={() => setIsVideoOpen(true)}>
                  Vào lại cuộc họp
                </Button>
              )
            ) : (
              // 2. Nếu là SINH VIÊN
              activeMeeting ? (
                // Nếu đang có cuộc họp -> Hiện nút Tham gia
                <Button type="primary" size="small" style={{ backgroundColor: '#52c41a' }} onClick={() => setIsVideoOpen(true)}>
                  Tham gia họp (Host: {activeMeeting.hostName})
                </Button>
              ) : (
                // Nếu không có họp -> Hiện Tag Offline
                <Tag color="default">Offline</Tag>
              )
            )}
          </div>
        </div>
      }
      style={{ height: "100%", display: "flex", flexDirection: "column" }}
      bodyStyle={{ flex: 1, display: "flex", flexDirection: "column", padding: "10px" }}
    >
      
      {/* ... Phần danh sách tin nhắn chat (giữ nguyên) ... */}

      {/* MODAL VIDEO CALL */}
      <Modal 
        title={activeMeeting ? `🎥 Đang họp với ${activeMeeting.hostName}` : "🎥 Cuộc họp"}
        open={isVideoOpen} 
        width={1000} 
        onCancel={() => setIsVideoOpen(false)} 
        footer={null} 
        destroyOnClose
      >
        <VideoCall 
            roomId={roomId} 
            username={user.fullName} 
            // 👇 TRUYỀN PASSWORD TỪ DATABASE SANG VIDEO CALL 👇
            password={activeMeeting?.password} 
            // 👇 onLeave phải là một HÀM xử lý logic 👇
            onLeave={user.role === 'LECTURER' ? handleEndMeeting : () => setIsVideoOpen(false)} 
        />
      </Modal>
    </Card>
  );
};
export default ChatRoom;