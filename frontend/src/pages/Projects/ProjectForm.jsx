import React, { useMemo, useState } from 'react'
import { Button, Card, Divider, Form, Input, Modal, Select, Space, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'
import { createProject } from '../../services/projectService'

const { Title, Text } = Typography
const { TextArea } = Input

export default function ProjectForm() {
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)

  // mock auth
  const [mockRole, setMockRole] = useState('LECTURER')
  const [mockUserId, setMockUserId] = useState('lec01')
  const mock = useMemo(() => ({ role: mockRole, userId: mockUserId }), [mockRole, mockUserId])

  const onFinish = async (values) => {
    setSubmitting(true)
    try {
      await createProject(
        {
          title: values.title?.trim(),
          syllabusId: values.syllabusId?.trim() || null,
          description: values.description?.trim() || '',
        },
        mock
      )
      Modal.success({ title: 'OK', content: 'Tạo dự án thành công!' })
      navigate('/projects')
    } catch (e) {
      Modal.error({ title: 'Lỗi', content: e?.response?.data?.message || e.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ maxWidth: 980 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
        <Title level={3} style={{ margin: 0 }}>
          Tạo dự án mẫu mới
        </Title>
        <Button onClick={() => navigate('/projects')}>← Quay lại</Button>
      </div>

      <Divider />

      <Card size="small" style={{ marginBottom: 12 }}>
        <Space wrap align="center">
          <Text strong>Mock Auth:</Text>
          <Text>Role</Text>
          <Select
            value={mockRole}
            style={{ width: 160 }}
            options={[
              { value: 'LECTURER', label: 'LECTURER' },
              { value: 'HEAD', label: 'HEAD' },
              { value: 'STUDENT', label: 'STUDENT' },
            ]}
            onChange={setMockRole}
          />
          <Text>UserId</Text>
          <Input value={mockUserId} onChange={(e) => setMockUserId(e.target.value)} style={{ width: 200 }} />
          <Text type="secondary">Headers gửi: X-ROLE / X-USER-ID</Text>
        </Space>
      </Card>

      <Card>
        <Form layout="vertical" onFinish={onFinish}>
          <Form.Item
            label="Tiêu đề dự án"
            name="title"
            rules={[{ required: true, message: 'Vui lòng nhập tiêu đề dự án' }]}
          >
            <Input placeholder="Ví dụ: Hệ thống quản lý lớp học PBL" />
          </Form.Item>

          <Form.Item label="Mã đề cương (Syllabus ID)" name="syllabusId">
            <Input placeholder="Ví dụ: SYL-SE101" />
          </Form.Item>

          <Form.Item label="Mô tả chi tiết" name="description">
            <TextArea rows={8} placeholder="Mô tả mục tiêu, phạm vi, yêu cầu, milestone..." />
          </Form.Item>

          <Space>
            <Button type="primary" htmlType="submit" loading={submitting}>
              {submitting ? 'Đang lưu...' : 'Lưu dự án'}
            </Button>
            <Button onClick={() => navigate('/projects')} disabled={submitting}>
              Hủy
            </Button>
          </Space>
        </Form>
      </Card>
    </div>
  )
}
