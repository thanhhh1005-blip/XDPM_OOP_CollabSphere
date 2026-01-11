import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function ProjectForm() {
  const navigate = useNavigate();

  // ✅ FIX: gọi thẳng Gateway (đừng gọi /api/... vì 5173 sẽ 404 nếu chưa proxy)
  const API_BASE_URL = "http://localhost:8080/api/v1/projects";

  const [title, setTitle] = useState("");
  const [syllabusId, setSyllabusId] = useState("");
  const [description, setDescription] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();

    if (!title.trim()) {
      alert("Vui lòng nhập tiêu đề dự án.");
      return;
    }

    try {
      setSubmitting(true);

      // ✅ (khuyến nghị) gửi header role/userId để BE chặn đúng quyền
      const role = localStorage.getItem("user_role");
      const userId = localStorage.getItem("user_id");
      const token = localStorage.getItem("access_token") || localStorage.getItem("token");

      await axios.post(
        API_BASE_URL,
        {
          title: title.trim(),
          syllabusId: syllabusId.trim() || null,
          description: description?.trim() || "",
        },
        {
          headers: {
            "Content-Type": "application/json",
            ...(role ? { "X-ROLE": role } : {}),
            ...(userId ? { "X-USER-ID": userId } : {}),
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
          },
        }
      );

      alert("Tạo dự án thành công!");
      navigate("/projects");
    } catch (error) {
      alert("Lỗi: " + (error.response?.data?.message || error.message));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.container}>
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

        <div style={styles.card}>
          <form onSubmit={onSubmit} style={styles.form}>
            <div style={styles.field}>
              <label style={styles.label}>
                Tiêu đề dự án <span style={styles.required}>*</span>
              </label>
              <input
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Ví dụ: Hệ thống quản lý lớp học PBL"
                style={styles.input}
              />
              <div style={styles.hint}>
                Tên ngắn gọn, dễ hiểu để hiển thị trong danh sách.
              </div>
            </div>

            <div style={styles.field}>
              <label style={styles.label}>Mã đề cương (Syllabus ID)</label>
              <input
                value={syllabusId}
                onChange={(e) => setSyllabusId(e.target.value)}
                placeholder="Ví dụ: SYL-SE101"
                style={styles.input}
              />
              <div style={styles.hint}>
                Có thể để trống nếu chưa liên kết đề cương.
              </div>
            </div>

            <div style={styles.field}>
              <label style={styles.label}>Mô tả chi tiết</label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Mô tả mục tiêu, phạm vi, yêu cầu, milestone..."
                style={styles.textarea}
              />
              <div style={styles.hint}>
                Bạn có thể dán đề cương dài, phần danh sách sẽ chỉ hiện rút gọn.
              </div>
            </div>

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

        {/* ✅ ĐÃ XÓA dòng hiển thị API ở đây */}
      </div>
    </div>
  );
}

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
  },
  backBtn: {
    border: "1px solid #d1d5db",
    background: "#fff",
    borderRadius: 10,
    padding: "10px 12px",
    cursor: "pointer",
    fontWeight: 700,
  },
  card: {
    background: "#fff",
    border: "1px solid #e5e7eb",
    borderRadius: 14,
    padding: 18,
    boxShadow: "0 10px 24px rgba(0,0,0,0.06)",
  },
  form: {
    display: "grid",
    gap: 14,
  },
  field: {
    display: "grid",
    gap: 8,
  },
  label: {
    fontWeight: 800,
    color: "#111827",
  },
  required: {
    color: "#ef4444",
  },
  input: {
    width: "100%",
    padding: "12px 12px",
    borderRadius: 10,
    border: "1px solid #d1d5db",
    outline: "none",
    fontSize: 14,
    background: "#fff",
  },
  textarea: {
    width: "100%",
    minHeight: 170,
    resize: "vertical",
    padding: "12px 12px",
    borderRadius: 10,
    border: "1px solid #d1d5db",
    outline: "none",
    fontSize: 14,
    background: "#fff",
    lineHeight: 1.4,
  },
  hint: {
    fontSize: 12,
    color: "#6b7280",
  },
  actions: {
    display: "flex",
    gap: 10,
    marginTop: 6,
  },
  primaryBtn: {
    border: "none",
    background: "#2563eb",
    color: "#fff",
    borderRadius: 10,
    padding: "10px 14px",
    fontWeight: 800,
  },
  secondaryBtn: {
    border: "1px solid #d1d5db",
    background: "#fff",
    borderRadius: 10,
    padding: "10px 14px",
    cursor: "pointer",
    fontWeight: 800,
  },
};
