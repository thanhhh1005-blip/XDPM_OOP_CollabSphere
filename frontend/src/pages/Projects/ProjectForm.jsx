import React, { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function ProjectForm() {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  // 🔗 CẤU HÌNH API GATEWAY
  const API_BASE_URL = "http://localhost:8080/api/v1/projects";

  // --- STATE QUẢN LÝ DỮ LIỆU ---
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState(""); // Mô tả ngắn
  const [syllabusContent, setSyllabusContent] = useState(""); // Nội dung đề cương

  // --- STATE QUẢN LÝ LỖI (VALIDATION) ---
  const [errors, setErrors] = useState({
    title: "",
    description: "",
    syllabusContent: ""
  });

  // --- STATE UI ---
  const [submitting, setSubmitting] = useState(false);
  const [importing, setImporting] = useState(false);

  // --- AUTH LOGIC (Lấy Token & Role từ LocalStorage) ---
  const savedUser = JSON.parse(localStorage.getItem("user") || "{}");
  const token =
    localStorage.getItem("accessToken") ||
    localStorage.getItem("token") ||
    savedUser?.token;
  const role = savedUser?.role || localStorage.getItem("role");
  const userId = savedUser?.id || localStorage.getItem("userId");

  // Hàm tạo Header cho Request
  const getHeaders = (isMultipart = false) => ({
    "Content-Type": isMultipart ? "multipart/form-data" : "application/json",
    ...(role ? { "X-ROLE": role } : {}),
    ...(userId ? { "X-USER-ID": String(userId) } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  });

  // --- HÀM KIỂM TRA DỮ LIỆU (VALIDATE) ---
  const validate = () => {
    let isValid = true;
    const newErrors = { title: "", description: "", syllabusContent: "" };

    if (!title.trim()) {
      newErrors.title = "Vui lòng nhập tiêu đề dự án.";
      isValid = false;
    }
    if (!description.trim()) {
      newErrors.description = "Vui lòng nhập mô tả ngắn.";
      isValid = false;
    }
    if (!syllabusContent.trim()) {
      newErrors.syllabusContent = "Vui lòng nhập nội dung đề cương hoặc import từ Excel.";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  // --- XỬ LÝ IMPORT FILE (GỌI QUA NIFI) ---
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Reset input để chọn lại file cùng tên vẫn trigger sự kiện change
    e.target.value = null;

    try {
      setImporting(true);
      // Xóa lỗi cũ của ô syllabus nếu có
      setErrors((prev) => ({ ...prev, syllabusContent: "" }));

      const formData = new FormData();
      formData.append("file", file);

      // Gọi API Backend -> Backend gọi NiFi -> Trả về Text
      const res = await axios.post(
        `${API_BASE_URL}/import-syllabus`, 
        formData, 
        { headers: getHeaders(true) }
      );

      if (res.data) {
        setSyllabusContent(res.data);
        alert("Đã import nội dung từ Excel thành công!");
      }
    } catch (error) {
      console.error(error);
      alert("Lỗi import: " + (error.response?.data?.message || error.message));
    } finally {
      setImporting(false);
    }
  };

  // --- XỬ LÝ SUBMIT FORM ---
  const onSubmit = async (e) => {
    e.preventDefault();

    // 1. Validate trước khi gửi
    if (!validate()) {
      return;
    }

    try {
      setSubmitting(true);
      
      // 2. Gọi API tạo dự án
      await axios.post(
        API_BASE_URL,
        {
          title: title.trim(),
          description: description.trim(),
          syllabusContent: syllabusContent.trim(),
        },
        { headers: getHeaders(false) }
      );

      alert("Tạo dự án thành công!");
      navigate("/projects");
    } catch (error) {
      alert("Lỗi: " + (error.response?.data?.message || error.message));
    } finally {
      setSubmitting(false);
    }
  };

  // --- GIAO DIỆN ---
  return (
    <div style={styles.page}>
      <div style={styles.container}>
        {/* Header Page */}
        <div style={styles.headerRow}>
          <h1 style={styles.title}>Tạo dự án mẫu mới</h1>
          <button
            type="button"
            onClick={() => navigate("/projects")}
            style={styles.backBtn}
          >
            ← Quay lại danh sách
          </button>
        </div>

        {/* Card Form */}
        <div style={styles.card}>
          <form onSubmit={onSubmit} style={styles.form}>
            
            {/* 1. TIÊU ĐỀ */}
            <div style={styles.field}>
              <label style={styles.label}>
                Tiêu đề dự án <span style={styles.required}>*</span>
              </label>
              <input
                value={title}
                onChange={(e) => {
                   setTitle(e.target.value);
                   setErrors((prev) => ({...prev, title: ""})); // Xóa lỗi khi nhập
                }}
                placeholder="Ví dụ: Hệ thống quản lý lớp học PBL"
                style={{
                    ...styles.input,
                    borderColor: errors.title ? "#ef4444" : "#d1d5db" // Viền đỏ nếu lỗi
                }}
              />
              {errors.title && <span style={styles.errorText}>{errors.title}</span>}
              <div style={styles.hint}>
                Tên ngắn gọn, dễ hiểu để hiển thị trong danh sách.
              </div>
            </div>

            {/* 2. MÔ TẢ NGẮN */}
            <div style={styles.field}>
              <label style={styles.label}>
                  Mô tả ngắn <span style={styles.required}>*</span>
              </label>
              <input
                value={description}
                onChange={(e) => {
                    setDescription(e.target.value);
                    setErrors((prev) => ({...prev, description: ""}));
                }}
                placeholder="Mô tả tóm tắt về dự án..."
                style={{
                    ...styles.input,
                    borderColor: errors.description ? "#ef4444" : "#d1d5db"
                }}
              />
              {errors.description && <span style={styles.errorText}>{errors.description}</span>}
            </div>

            {/* 3. NỘI DUNG SYLLABUS + IMPORT EXCEL */}
            <div style={styles.field}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <label style={styles.label}>
                    Nội dung Đề cương / Syllabus <span style={styles.required}>*</span>
                </label>
                
                {/* NÚT IMPORT EXCEL */}
                <div>
                  <input
                    type="file"
                    accept=".xlsx, .xls"
                    ref={fileInputRef}
                    style={{ display: "none" }}
                    onChange={handleFileChange}
                  />
                  <button
                    type="button"
                    onClick={() => fileInputRef.current.click()}
                    disabled={importing}
                    style={{
                      ...styles.secondaryBtn,
                      fontSize: 12,
                      padding: "6px 12px",
                      background: importing ? "#e5e7eb" : "#ecfdf5",
                      color: importing ? "#9ca3af" : "#059669",
                      borderColor: "#10b981",
                      display: "flex",
                      alignItems: "center",
                      gap: 6
                    }}
                  >
                    {importing ? "⏳ Đang xử lý qua NiFi..." : "📂 Import từ Excel"}
                  </button>
                </div>
              </div>

              <textarea
                value={syllabusContent}
                onChange={(e) => {
                    setSyllabusContent(e.target.value);
                    setErrors((prev) => ({...prev, syllabusContent: ""}));
                }}
                placeholder="Nhập chi tiết các tuần học, yêu cầu kỹ thuật... (Hoặc import từ file Excel)"
                style={{
                    ...styles.textarea,
                    borderColor: errors.syllabusContent ? "#ef4444" : "#d1d5db"
                }}
              />
              {errors.syllabusContent && <span style={styles.errorText}>{errors.syllabusContent}</span>}
              <div style={styles.hint}>
                Nội dung này sẽ được AI sử dụng để tạo cột mốc.
              </div>
            </div>

            {/* ACTIONS BUTTONS */}
            <div style={styles.actions}>
              <button
                type="submit"
                disabled={submitting}
                style={{
                  ...styles.primaryBtn,
                  opacity: submitting ? 0.7 : 1,
                  cursor: submitting ? "not-allowed" : "pointer",
                }}
              >
                {submitting ? "Đang lưu..." : "Lưu dự án"}
              </button>

              <button
                type="button"
                disabled={submitting}
                onClick={() => navigate("/projects")}
                style={styles.secondaryBtn}
              >
                Hủy
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

// --- STYLES ---
const styles = {
  page: {
    minHeight: "100vh",
    background: "#f6f7fb",
    padding: "28px 16px",
  },
  container: {
    width: "min(980px, 100%)",
    margin: "0 auto",
  },
  headerRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 12,
    marginBottom: 14,
  },
  title: {
    margin: 0,
    fontSize: 36,
    fontWeight: 900,
    letterSpacing: "-0.02em",
    color: "#111827",
  },
  backBtn: {
    border: "1px solid #d1d5db",
    background: "#fff",
    borderRadius: 10,
    padding: "10px 12px",
    cursor: "pointer",
    fontWeight: 700,
    color: "#374151",
  },
  card: {
    background: "#fff",
    border: "1px solid #e5e7eb",
    borderRadius: 14,
    padding: 24,
    boxShadow: "0 10px 24px rgba(0,0,0,0.06)",
  },
  form: {
    display: "grid",
    gap: 20,
  },
  field: {
    display: "grid",
    gap: 8,
  },
  label: {
    fontWeight: 800,
    color: "#111827",
    fontSize: "14px",
  },
  required: {
    color: "#ef4444",
    marginLeft: 4,
  },
  input: {
    width: "100%",
    padding: "12px",
    borderRadius: 10,
    border: "1px solid #d1d5db",
    outline: "none",
    fontSize: 14,
    background: "#fff",
    transition: "border-color 0.2s",
  },
  textarea: {
    width: "100%",
    minHeight: 200,
    resize: "vertical",
    padding: "12px",
    borderRadius: 10,
    border: "1px solid #d1d5db",
    outline: "none",
    fontSize: 14,
    background: "#fff",
    lineHeight: 1.5,
    fontFamily: "inherit",
    transition: "border-color 0.2s",
  },
  hint: {
    fontSize: 12,
    color: "#6b7280",
    marginTop: 4,
  },
  errorText: {
    fontSize: 12,
    color: "#ef4444",
    fontWeight: 600,
  },
  actions: {
    display: "flex",
    gap: 12,
    marginTop: 10,
  },
  primaryBtn: {
    border: "none",
    background: "#2563eb",
    color: "#fff",
    borderRadius: 10,
    padding: "12px 20px",
    fontWeight: 800,
    fontSize: 14,
    transition: "opacity 0.2s",
  },
  secondaryBtn: {
    border: "1px solid #d1d5db",
    background: "#fff",
    borderRadius: 10,
    padding: "12px 20px",
    cursor: "pointer",
    fontWeight: 800,
    fontSize: 14,
    color: "#374151",
  },
};