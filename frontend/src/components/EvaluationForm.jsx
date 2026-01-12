import { Form, Input, Button, Rate, message, Divider } from "antd";
import { useState } from "react";
import { createEvaluation } from "../services/evaluationApi";
import EvaluationHistory from "./EvaluationHistory";

const EvaluationForm = ({ type }) => {
  const [form] = Form.useForm();
  const [reloadKey, setReloadKey] = useState(0);

  /* ===== Chuẩn hoá payload theo BACKEND ===== */
  const normalizePayload = (type, v) => {
    switch (type) {
      case "team":
        return {
          teamId: Number(v.teamId),
          evaluatorId: Number(v.evaluatorId),
          score: v.score,
          comment: v.comment,
        };

      case "checkpoint":
        return {
          checkpointId: Number(v.checkpointId),
          evaluatorId: Number(v.evaluatorId),
          score: v.score,
          comment: v.comment,
        };

      case "member":
        return {
          memberId: Number(v.memberId),
          evaluatorId: Number(v.evaluatorId),
          score: v.score,
          comment: v.comment,
        };

      case "peer":
        return {
          fromStudentId: Number(v.fromStudentId),
          toStudentId: Number(v.toStudentId),
          score: v.score,
          comment: v.comment,
        };

      default:
        return {};
    }
  };

  /* ===== Submit ===== */
  const onFinish = async (values) => {
    try {
      await createEvaluation(type, normalizePayload(type, values));
      message.success("✅ Gửi đánh giá thành công");
      form.resetFields();
      setReloadKey((k) => k + 1); // reload history
    } catch (e) {
      console.error(e);
      message.error("❌ Gửi đánh giá thất bại");
    }
  };

  return (
    <>
      <Form form={form} layout="vertical" onFinish={onFinish}>
        {/* ===== TARGET ===== */}
        {type === "team" && (
          <Form.Item name="teamId" label="Team ID" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
        )}

        {type === "checkpoint" && (
          <Form.Item
            name="checkpointId"
            label="Checkpoint ID"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
        )}

        {type === "member" && (
          <Form.Item
            name="memberId"
            label="Member ID"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
        )}

        {type === "peer" && (
          <>
            <Form.Item
              name="fromStudentId"
              label="From Student ID"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
              name="toStudentId"
              label="To Student ID"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
          </>
        )}

        {/* ===== COMMON ===== */}
        {type !== "peer" && (
          <Form.Item
            name="evaluatorId"
            label="Evaluator ID"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
        )}

        <Form.Item name="score" label="Score" rules={[{ required: true }]}>
          <Rate count={10} />
        </Form.Item>

        <Form.Item name="comment" label="Comment">
          <Input.TextArea rows={3} />
        </Form.Item>

        <Button type="primary" htmlType="submit">
          Gửi đánh giá
        </Button>
      </Form>

      <Divider>📜 Lịch sử đánh giá</Divider>

      <EvaluationHistory type={type} reloadKey={reloadKey} />
    </>
  );
};

export default EvaluationForm;
