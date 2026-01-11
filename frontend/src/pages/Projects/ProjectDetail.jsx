import React, { useEffect, useMemo, useState } from 'react'
import { Button, Card, Modal, Space, Typography, Select, Input } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { getProjectById } from '../../services/projectService'

const { Title, Text } = Typography

export default function ProjectDetail() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [mockRole, setMockRole] = useState('LECTURER')
  const [mockUserId, setMockUserId] = useState('lec01')
  const mock = useMemo(() => ({ role: mockRole, userId: mockUserId }), [mockRole, mockUserId])

  const [loading, setLoading] = useState(false)
  const [project, setProject] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const data = await getProjectById(id, mock)
      setProject(data)
    } catch (e) {
      Modal.error({ title: 'Lỗi', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, mockRole, mockUserId])

  return (
    <div style={{ maxWidth: 980 }}>
      <Space style={{ justifyContent: 'space-between', width: '100%' }} align="center">
        <Title level={3} style={{ margin: 0 }}>Chi tiết dự án</Title>
        <Button onClick={() => navigate('/projects')}>← Quay lại</Button>
      </Space>

      <Card size="small" style={{ marginTop: 12, marginBottom: 12 }}>
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
        </Space>
      </Card>

      <Card loading={loading}>
        <div style={{ marginBottom: 8 }}><Text type="secondary">ID: {project?.id}</Text></div>
        <div style={{ marginBottom: 8 }}><Text strong>Title:</Text> {project?.title}</div>
        <div style={{ marginBottom: 8 }}><Text strong>Status:</Text> {project?.status}</div>

        <pre style={{ background: '#f6f7fb', border: '1px solid #eee', borderRadius: 10, padding: 14, whiteSpace: 'pre-wrap' }}>
          {project?.description || '(Không có mô tả)'}
        </pre>
      </Card>
    </div>
  )
}