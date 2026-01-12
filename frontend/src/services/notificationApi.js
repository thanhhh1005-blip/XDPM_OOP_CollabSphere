// 1. Kiểm tra kỹ URL này. Nếu Controller backend là "/notifications" thì bỏ "/api" đi
const GATEWAY_URL = "http://localhost:8080/api/notifications"; 

export async function getNotifications(userId) {
  const response = await fetch(`${GATEWAY_URL}/user/${userId}`);
  
  if (!response.ok) {
    // Ném lỗi để component bên ngoài (NotificationPage) bắt được và log ra
    throw new Error(`Lỗi tải thông báo: ${response.statusText}`);
  }
  
  return response.json();
}

export async function markAsRead(notificationId) {
  const response = await fetch(`${GATEWAY_URL}/${notificationId}/read`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
  });

  // 2. Thêm kiểm tra lỗi cho hàm này
  if (!response.ok) {
    throw new Error(`Không thể đánh dấu đã đọc: ${response.statusText}`);
  }
  
  // Hàm PUT này thường backend trả về void hoặc string, 
  // không cần return json() trừ khi backend trả về đối tượng đã update
  return true; 
}