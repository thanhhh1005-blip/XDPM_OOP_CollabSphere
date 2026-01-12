import { useEffect, useState } from "react";
import { message } from "antd"; // Dùng message của Antd để báo lỗi/thành công
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import NotificationList from "../../components/NotificationList";

// URL API
const API_URL = "http://localhost:8080/api/notifications";

export default function NotificationPage({ userId }) {
  const [notifications, setNotifications] = useState([]);

  // 1. Load danh sách (Dùng fetch)
  useEffect(() => {
    if (!userId) return;
    fetch(`${API_URL}/user/${userId}`)
      .then((res) => res.json())
      .then((data) => {
        if (Array.isArray(data)) setNotifications(data);
      })
      .catch((err) => console.error(err));
  }, [userId]);

  // 2. Real-time (WebSocket)
  useEffect(() => {
    if (!userId) return;
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/user/${userId}/notifications`, (msg) => {
        if (msg.body) {
          const newNotif = JSON.parse(msg.body);
          setNotifications((prev) => [newNotif, ...prev]);
          message.info("Có thông báo mới!");
        }
      });
    });

    return () => {
      if (stompClient?.connected) stompClient.disconnect();
    };
  }, [userId]);

  // 3. Hàm xử lý khi bấm nút "Đã đọc" (Truyền xuống List -> Item)
  const handleRead = (id) => {
    fetch(`${API_URL}/${id}/read`, { method: "PUT" })
      .then((res) => {
        if (res.ok) {
          // Cập nhật state ngay lập tức để giao diện đổi màu
          setNotifications((prev) =>
            prev.map((n) => (n.id === id ? { ...n, read: true } : n))
          );
          message.success("Đã đánh dấu đã đọc");
        }
      })
      .catch(() => message.error("Lỗi kết nối"));
  };

  return (
    <div style={{ padding: "24px", maxWidth: "800px", margin: "0 auto" }}>
      <h2 style={{ marginBottom: 20, fontWeight: "bold" }}>Trung tâm thông báo</h2>
      
      {/* Truyền notifications VÀ hàm handleRead xuống */}
      <NotificationList 
        notifications={notifications} 
        onRead={handleRead} 
      />
    </div>
  );
}