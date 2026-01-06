import React, { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { Input, Button, List, Card, Avatar, Tag } from "antd";
import { SendOutlined, UserOutlined } from "@ant-design/icons";
import { Modal } from "antd"; // Thêm Modal
import { VideoCameraOutlined } from "@ant-design/icons"; // Thêm icon Camera
import VideoCall from "./VideoCall"; // Import component vừa tạo

const ChatRoom = () => {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isVideoOpen, setIsVideoOpen] = useState(false);

  // Tự tạo tên ngẫu nhiên để test
  const username = useRef("User_" + Math.floor(Math.random() * 100));
  const roomId = 1;

  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8080/ws", // Cổng Communication Service
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

  const sendMessage = () => {
    if (inputText.trim() && stompClient && isConnected) {
      const chatMessage = {
        senderName: username.current,
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
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <span>
            💬 Chat{" "}
            {isConnected ? (
              <Tag color="green">Online</Tag>
            ) : (
              <Tag color="red">Offline</Tag>
            )}
          </span>
          {/* Nút Gọi Video */}
          <Button
            type="primary"
            danger
            shape="round"
            icon={<VideoCameraOutlined />}
            onClick={() => {
              // Tạo tên phòng duy nhất
              const roomName = `CollabSphere_Meeting_${roomId}`;
              // Mở sang tab mới
              window.open(`https://meet.jit.si/${roomName}`, "_blank");
            }}
          >
            Họp Nhóm
          </Button>
        </div>
      }
      style={{ height: "100%", display: "flex", flexDirection: "column" }}
      bodyStyle={{ flex: 1, display: "flex", flexDirection: "column" }}
    >
      <div
        style={{
          flex: 1,
          overflowY: "auto",
          marginBottom: "10px",
          paddingRight: "10px",
        }}
      >
        <List
          dataSource={messages}
          renderItem={(msg) => {
            const isMe = msg.senderName === username.current;
            return (
              <div
                style={{
                  display: "flex",
                  justifyContent: isMe ? "flex-end" : "flex-start",
                  marginBottom: "10px",
                }}
              >
                <div
                  style={{
                    backgroundColor: isMe ? "#1890ff" : "#f0f2f5",
                    color: isMe ? "white" : "black",
                    padding: "8px 12px",
                    borderRadius: "15px",
                    maxWidth: "70%",
                  }}
                >
                  <div
                    style={{
                      fontSize: "10px",
                      opacity: 0.7,
                      marginBottom: "2px",
                    }}
                  >
                    {msg.senderName}
                  </div>
                  {msg.content}
                </div>
              </div>
            );
          }}
        />
      </div>

      <div style={{ display: "flex", gap: "8px" }}>
        <Input
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onPressEnter={sendMessage}
          placeholder="Nhập tin nhắn..."
        />
        <Button type="primary" icon={<SendOutlined />} onClick={sendMessage} />
      </div>
      <Modal
        title="🎥 Phòng Họp Trực Tuyến"
        open={isVideoOpen}
        onCancel={() => setIsVideoOpen(false)}
        footer={null} // Không hiện nút OK/Cancel của Modal
        width={1000} // Mở rộng chiều ngang
        destroyOnClose // Tắt Modal là tắt Video
      >
        {isVideoOpen && (
          <VideoCall
            roomId={roomId}
            username={username.current} // Dùng tên user hiện tại
            onLeave={() => setIsVideoOpen(false)} // Tắt modal khi bấm gác máy
          />
        )}
      </Modal>
    </Card>
  );
};
export default ChatRoom;
