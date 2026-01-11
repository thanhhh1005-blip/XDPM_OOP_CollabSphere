import React, { useEffect, useMemo, useState } from 'react'
import { Button, Card, Divider, Modal, Select, Space, Table, Tag, Typography, Input } from 'antd'
import { useNavigate } from 'react-router-dom'
import {
  approveProject,
  assignProject,
  denyProject,
  getProjects,
  submitProject,
} from '../../services/projectService'

const { Title, Text } = Typography

const statusTag = (s) => {
  if (s === 'APPROVED') return <Tag color="green">APPROVED</Tag>
  if (s === 'PENDING') return <Tag color="gold">PENDING</Tag>
  if (s === 'DENIED') return <Tag color="red">DENIED</Tag>
  return <Tag>DRAFT</Tag>
}

export default function ProjectList() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [projects, setProjects] = useState([])

  // mock role giống Team FE
  const [mockRole, setMockRole] = useState('LECTURER')
  const [mockUserId, setMockUserId] = useState('lec01')

  // modal mô tả
  const [open, setOpen] = useState(false)
  const [selected, setSelected] = useState(null)

  const mock = useMemo(() => ({ role: mockRole, userId: mockUserId }), [mockRole, mockUserId])

  const fetchData = async () => {
    setLoading(true)
    try {
      const data = await getProjects(mock)
      setProjects(Array.isArray(data) ? data : [])
    } catch (e) {
      Modal.error({ title: 'Lỗi', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mockRole, mockUserId])

  const openDesc = (p) => {
    setSelected(p)
    setOpen(true)
  }

  const doSubmit = async (id) => {
    setLoading(true)
    try {
      await submitProject(id, mock)
      await fetchData()
    } catch (e) {
      Modal.error({ title: 'Lỗi submit', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  const doApprove = async (id) => {
    setLoading(true)
    try {
      await approveProject(id, mock)
      await fetchData()
    } catch (e) {
      Modal.error({ title: 'Lỗi approve', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  const doDeny = async (id) => {
    setLoading(true)
    try {
      await denyProject(id, mock)
      await fetchData()
    } catch (e) {
      Modal.error({ title: 'Lỗi deny', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  const doAssign = async (id) => {
    const classId = prompt('Nhập mã lớp để giao dự án:')
    if (!classId) return
    setLoading(true)
    try {
      await assignProject(id, classId, mock)
      await fetchData()
    } catch (e) {
      Modal.error({ title: 'Lỗi assign', content: e?.response?.data?.message || e.message })
    } finally {
      setLoading(false)
    }
  }

  const columns = [
    {
      title: 'Tiêu đề',
      dataIndex: 'title',
      key: 'title',
      width: 260,
      render: (t) => (
        <Text strong ellipsis style={{ maxWidth: 240, display: 'inline-block' }}>
          {t}
        </Text>
      ),
    },
    {
      title: 'Mô tả',
      dataIndex: 'description',
      key: 'description',
      // làm cột mô tả “vừa mắt”: 1 dòng + nút xem
      render: (_, row) => (
        <div style={{ maxWidth: 520 }}>
          <div
            style={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
            title="Bấm để xem đầy đủ"
            onClick={() => openDesc(row)}
          >
            {row.description || '(Không có mô tả)'}
          </div>
          <Button type="link" size="small" onClick={() => openDesc(row)} style={{ padding: 0 }}>
            Xem đầy đủ
          </Button>
        </div>
      ),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      align: 'center',
      render: (s) => statusTag(s),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 160, // ✅ cột thao tác nhỏ lại
      align: 'center',
      render: (_, row) => {
        return (
          <Space direction="vertical" style={{ width: '100%' }} size={8}>
            {row.status === 'DRAFT' && (
              <Button block size="small" onClick={() => doSubmit(row.id)}>
                Nộp duyệt
              </Button>
            )}

            {row.status === 'PENDING' && (
              <>
                <Button block size="small" type="primary" onClick={() => doApprove(row.id)}>
                  Duyệt
                </Button>
                <Button block size="small" danger onClick={() => doDeny(row.id)}>
                  Từ chối
                </Button>
              </>
            )}

            {row.status === 'APPROVED' && (
              <Button block size="small" type="primary" onClick={() => doAssign(row.id)}>
                Giao lớp
              </Button>
            )}

            {/* Nếu cần xem detail page theo id */}
            {/* <Button block size="small" onClick={() => navigate(`/projects/${row.id}`)}>Chi tiết</Button> */}
          </Space>
        )
      },
    },
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, alignItems: 'center' }}>
        <Title level={3} style={{ margin: 0 }}>
          Danh sách Dự án CollabSphere
        </Title>

        <Button type="primary" onClick={() => navigate('/projects/new')}>
          + Tạo Dự án Mẫu
        </Button>
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

          <Button onClick={fetchData} loading={loading}>
            Refresh
          </Button>
        </Space>
      </Card>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={projects}
        pagination={{ pageSize: 8 }}
        scroll={{ x: 900 }}
      />

      <Modal
        open={open}
        onCancel={() => setOpen(false)}
        footer={null}
        title={selected?.title || 'Mô tả'}
        width={900}
      >
        <div style={{ marginBottom: 8 }}>
          <Text type="secondary">ID: {selected?.id}</Text>
        </div>
        <pre
          style={{
            margin: 0,
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-word',
            background: '#f6f7fb',
            border: '1px solid #eee',
            borderRadius: 10,
            padding: 14,
            maxHeight: '60vh',
            overflow: 'auto',
            lineHeight: 1.5,
          }}
        >
          {selected?.description || '(Không có mô tả)'}
        </pre>
      </Modal>
    </div>
  )
}
