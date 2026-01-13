import React from "react";
import { Progress } from "antd";

const TeamProgress = ({ team }) => {
  if (!team) {
    return <div>Không có dữ liệu tiến độ</div>;
  }

  // Fallback an toàn
  const progress =
    team.progress ??
    team.completionPercentage ??
    team.percent ??
    0;

  return (
    <div>
      <h3 style={{ marginBottom: 12 }}>Tiến độ Team</h3>

      <Progress
        percent={progress}
        status={progress === 100 ? "success" : "active"}
        strokeWidth={12}
      />

      <div style={{ marginTop: 8, color: "#6b7280", fontSize: 13 }}>
        {progress === 100
          ? "🎉 Team đã hoàn thành dự án"
          : "Team đang trong quá trình thực hiện"}
      </div>
    </div>
  );
};

export default TeamProgress;
