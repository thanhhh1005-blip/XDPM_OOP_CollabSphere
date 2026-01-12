const API_URL = "http://localhost:8080/api/evaluations";

/* ================= 1. CREATE EVALUATION ================= */
export const createEvaluation = async (type, payload) => {
  try {
    const response = await fetch(`${API_URL}/${type}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || "Gửi đánh giá thất bại");
    }

    return await response.json();
  } catch (error) {
    console.error("Evaluation API error:", error);
    throw error;
  }
};

/* ================= 2. HISTORY ================= */
export const getTeamHistory = async (teamId) => {
  try {
    const res = await fetch(`${API_URL}/team/${teamId}`);
    if (!res.ok) throw new Error();
    return await res.json();
  } catch {
    return [];
  }
};

export const getCheckpointHistory = async (checkpointId) => {
  try {
    const res = await fetch(`${API_URL}/checkpoint/${checkpointId}`);
    if (!res.ok) throw new Error();
    return await res.json();
  } catch {
    return [];
  }
};

export const getMemberHistory = async (memberId) => {
  try {
    const res = await fetch(`${API_URL}/member/${memberId}`);
    if (!res.ok) throw new Error();
    return await res.json();
  } catch {
    return [];
  }
};

export const getPeerHistory = async (studentId) => {
  try {
    const res = await fetch(`${API_URL}/peer/${studentId}`);
    if (!res.ok) throw new Error();
    return await res.json();
  } catch {
    return [];
  }
};
