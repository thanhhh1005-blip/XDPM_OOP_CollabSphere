import React, { useState, useEffect } from "react";
import { Badge, Popover, List, Avatar, Typography, Button, message } from "antd";
import { BellOutlined, CheckCircleOutlined, UserOutlined } from "@ant-design/icons";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

// --- CẤU HÌNH API ---
const API_URL = "http://localhost:8080/api/notifications"; 
const WS_URL = "http://localhost:8080/ws/notifications";

const { Text } = Typography;

const NotificationBell = ({ userId }) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  // 1. Dùng FETCH lấy danh sách lịch sử
  useEffect(() => {
    if (!userId) return;

    // Thay axios.get bằng fetch
    fetch(`${API_URL}/user/${userId}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Lỗi khi gọi API");
        }
        return response.json(); // Phải chuyển đổi sang JSON thủ công
      })
      .then((data) => {
        // Đảm bảo data là mảng
        const list = Array.isArray(data) ? data : [];
        setNotifications(list);
        setUnreadCount(list.filter((n) => !n.read).length);
      })
      .catch((err) => console.error("Lỗi tải thông báo:", err));
  }, [userId]);

  // 2. Kết nối WebSocket (Giữ nguyên vì không liên quan axios)
  useEffect(() => {
    if (!userId) return;

    const socket = new SockJS(WS_URL);
    const stompClient = Stomp.over(socket);
    stompClient.debug = null; 

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/user/${userId}/notifications`, (msg) => {
        if (msg.body) {
          const newNotif = JSON.parse(msg.body);
          setNotifications((prev) => [newNotif, ...prev]);
          setUnreadCount((prev) => prev + 1);
          message.info("🔔 Bạn có thông báo mới!");
        }
      });
    });

    return () => {
      if (stompClient && stompClient.connected) stompClient.disconnect();
    };
  }, [userId]);

  // 3. Dùng FETCH để đánh dấu đã đọc (PUT)
  const handleRead = (id) => {
    fetch(`${API_URL}/${id}/read`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Không thể đánh dấu đã đọc");
        }
        // Cập nhật giao diện sau khi API thành công
        setNotifications((prevList) =>
          prevList.map((n) => (n.id === id ? { ...n, read: true } : n))
        );
        setUnreadCount((prev) => Math.max(0, prev - 1));
      })
      .catch((err) => {
        console.error("Lỗi:", err);
        message.error("Có lỗi xảy ra, vui lòng thử lại!");
      });
  };

  // --- GIAO DIỆN (ANT DESIGN) ---
  const notificationContent = (
    <div style={{ width: 350, maxHeight: 400, overflowY: "auto" }}>
      <List
        itemLayout="horizontal"
        dataSource={notifications}
        locale={{ emptyText: "Không có thông báo nào" }}
        renderItem={(item) => (
          <List.Item
            actions={[
              !item.read && (
                <Button
                  type="text"
                  icon={<CheckCircleOutlined style={{ color: "#1890ff" }} />}
                  onClick={() => handleRead(item.id)}
                  title="Đánh dấu đã đọc"
                />
              ),
            ]}
            style={{
              background: item.read ? "white" : "#e6f7ff", // Màu xanh nhạt nếu chưa đọc
              padding: "10px",
              borderRadius: "4px",
              marginBottom: "2px",
              cursor: "pointer",
              transition: "background 0.3s",
            }}
          >
            <List.Item.Meta
              avatar={
                <Avatar
                  style={{ backgroundColor: item.read ? "#ccc" : "#1890ff" }}
                  icon={<UserOutlined />}
                />
              }
              title={
                <Text strong={!item.read}>
                  {item.title || "Thông báo hệ thống"}
                </Text>
              }
              description={
                <div>
                  <div style={{ fontSize: "12px", color: "#555" }}>
                    {item.message}
                  </div>
                  <div style={{ fontSize: "10px", color: "#999", marginTop: "4px" }}>
                    {item.createdAt 
                      ? new Date(item.createdAt).toLocaleString("vi-VN") 
                      : "Vừa xong"}
                  </div>
                </div>
              }
            />
          </List.Item>
        )}
      />
    </div>
  );

  return (
    <Popover
      content={notificationContent}
      title="Thông báo"
      trigger="click"
      placement="bottomRight"
    >
      <Badge count={unreadCount} overflowCount={99} offset={[-2, 5]}>
        <Button
          shape="circle"
          icon={<BellOutlined style={{ fontSize: "20px", color: "#555" }} />}
          size="large"
          style={{ border: "none", background: "transparent", boxShadow: "none" }}
        />
      </Badge>
    </Popover>
  );
};

export default NotificationBell;