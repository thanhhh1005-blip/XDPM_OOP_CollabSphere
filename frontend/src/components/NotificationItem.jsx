import React from "react";
import { List, Avatar, Button, Typography, Tag, Tooltip } from "antd";
import { 
  CheckCircleOutlined, 
  ClockCircleOutlined, 
  BellOutlined, 
  ReadOutlined 
} from "@ant-design/icons";

const { Text, Paragraph } = Typography;

export default function NotificationItem({ notification, onRead }) {
  // Format ngày giờ cho đẹp (Ví dụ: 14:30 12/05/2024)
  const formattedDate = notification.createdAt 
    ? new Date(notification.createdAt).toLocaleString("vi-VN", {
        hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
      })
    : "Vừa xong";

  return (
    <List.Item
      // Các nút hành động bên phải
      actions={[
        !notification.read && (
          <Tooltip title="Đánh dấu đã đọc">
            <Button
              type="text"
              icon={<CheckCircleOutlined style={{ fontSize: 18, color: "#1890ff" }} />}
              onClick={() => onRead(notification.id)}
            />
          </Tooltip>
        ),
      ]}
      style={{
        padding: "16px",
        background: notification.read ? "#fff" : "#f0f5ff", // Màu xanh nhạt nếu chưa đọc
        borderLeft: notification.read ? "4px solid transparent" : "4px solid #1890ff", // Viền xanh đánh dấu
        transition: "all 0.3s",
        cursor: "pointer",
      }}
      className="hover:bg-gray-50" // Hiệu ứng hover (nếu có Tailwind)
    >
      <List.Item.Meta
        // Avatar bên trái
        avatar={
          <Avatar 
            style={{ backgroundColor: notification.read ? "#ccc" : "#1890ff" }} 
            icon={notification.read ? <ReadOutlined /> : <BellOutlined />} 
          />
        }
        // Tiêu đề thông báo
        title={
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <Text strong={!notification.read} style={{ fontSize: 16 }}>
              {notification.title || "Thông báo hệ thống"}
            </Text>
            {!notification.read && <Tag color="blue">Mới</Tag>}
          </div>
        }
        // Nội dung chi tiết
        description={
          <div>
            <Paragraph 
                ellipsis={{ rows: 2, expandable: true, symbol: 'Xem thêm' }} 
                style={{ margin: "4px 0", color: "#555" }}
            >
              {notification.message || notification.content} 
            </Paragraph>
            
            <div style={{ display: "flex", alignItems: "center", gap: 5, color: "#888", fontSize: 12 }}>
              <ClockCircleOutlined /> {formattedDate}
            </div>
          </div>
        }
      />
    </List.Item>
  );
}