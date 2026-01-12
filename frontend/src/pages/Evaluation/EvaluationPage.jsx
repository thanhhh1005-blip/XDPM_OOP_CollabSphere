import { Badge, Card, Tabs } from "antd";
import EvaluationForm from "../../components/EvaluationForm";

const EvaluationPage = () => {
  const items = [
    { key: "team", label: "🏆 Team", children: <EvaluationForm type="team" /> },
    {
      key: "checkpoint",
      label: "📍 Checkpoint",
      children: <EvaluationForm type="checkpoint" />,
    },
    {
      key: "member",
      label: "👤 Member",
      children: <EvaluationForm type="member" />,
    },
    {
      key: "peer",
      label: "🤝 Peer Review",
      children: <EvaluationForm type="peer" />,
    },
  ];

  return (
    <Card
      title={
        <>
          📊 Dịch vụ đánh giá <Badge count="LIVE" color="green" />
        </>
      }
    >
      <Tabs items={items} />
    </Card>
  );
};

export default EvaluationPage;
