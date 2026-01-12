import { Table, Empty } from "antd";
import { useEffect, useState } from "react";
import {
  getTeamHistory,
  getCheckpointHistory,
  getMemberHistory,
  getPeerHistory,
} from "../services/evaluationApi";

const EvaluationHistory = ({ type, reloadKey }) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadHistory();
    // eslint-disable-next-line
  }, [reloadKey]);

  const loadHistory = async () => {
    setLoading(true);

    let result = [];

    try {
      switch (type) {
        case "team":
          result = await getTeamHistory(1); // test nhanh
          break;
        case "checkpoint":
          result = await getCheckpointHistory(1);
          break;
        case "member":
          result = await getMemberHistory(1);
          break;
        case "peer":
          result = await getPeerHistory(1);
          break;
        default:
          result = [];
      }
    } finally {
      setData(result || []);
      setLoading(false);
    }
  };

  const columns = [
    { title: "ID", dataIndex: "id" },
    { title: "Score", dataIndex: "score" },
    { title: "Comment", dataIndex: "comment" },
    { title: "Evaluator", dataIndex: "evaluatorId" },
    { title: "Time", dataIndex: "createdAt" },
  ];

  if (!data.length) {
    return <Empty description="Chưa có đánh giá" />;
  }

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={data}
      loading={loading}
      pagination={{ pageSize: 5 }}
    />
  );
};

export default EvaluationHistory;
