// src/configs/firebase.js

// 1. Import các hàm cần thiết
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth"; // <--- THÊM DÒNG NÀY ĐỂ DÙNG ĐĂNG NHẬP

// 2. Cấu hình (Lấy từ hình bạn chụp)
const firebaseConfig = {
  apiKey: "AIzaSyDLNrw8mJMSVnP...", // (Copy y nguyên từ màn hình của bạn)
  authDomain: "practiceuploaddb.firebaseapp.com",
  projectId: "practiceuploaddb",
  storageBucket: "practiceuploaddb.firebasestorage.app", // (Lưu ý cái này để biết tên bucket)
  messagingSenderId: "1033355613154",
  appId: "1:1033355613154:web:...",
  measurementId: "G-Z9VSC2PSJW"
};

// 3. Khởi tạo Firebase
const app = initializeApp(firebaseConfig);

// 4. Xuất ra để dùng ở các trang khác
export const auth = getAuth(app); // Dùng cho Login
export default app;