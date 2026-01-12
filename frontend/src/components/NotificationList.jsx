import React from "react";
import { List, Empty } from "antd";
import NotificationItem from "./NotificationItem";

export default function NotificationList({ notifications, onRead }) {
  return (
    <div style={{ background: "#fff", borderRadius: 8, boxShadow: "0 2px 8px rgba(0,0,0,0.1)" }}>
      <List
        itemLayout="horizontal"
        dataSource={notifications}
        // Hiển thị khi không có thông báo nào
        locale={{
          emptyText: (
            <Empty 
                image={Empty.PRESENTED_IMAGE_SIMPLE} 
                description="Bạn chưa có thông báo nào" 
            />
          )
        }}
        // Render từng phần tử
        renderItem={(item) => (
          <NotificationItem 
            key={item.id} 
            notification={item} 
            onRead={onRead} 
          />
        )}
        // (Tùy chọn) Phân trang nếu danh sách quá dài
        pagination={{
            onChange: (page) => {
              console.log(page);
            },
            pageSize: 5, // Số lượng hiển thị trên 1 trang
            align: 'center'
        }}
      />
    </div>
  );
}