import React, { useEffect, useMemo, useState } from 'react';
import {
  Button,
  Upload,
  message,
  Spin,
  Space,
  Table,
  Popconfirm,
  Modal,
  Input,
  Tag,
} from 'antd';
import {
  UploadOutlined,
  DeleteOutlined,
  DownloadOutlined,
  ReloadOutlined,
  EyeOutlined,
  SearchOutlined,
} from '@ant-design/icons';

import {
  getResources,
  uploadResource,
  deleteResource,
  downloadResource,
} from '../../services/resourceService';

const { Search } = Input;

const ResourcePage = () => {
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [file, setFile] = useState(null);

  /* ===================== SEARCH ===================== */
  const [keyword, setKeyword] = useState('');

  /* ===================== PREVIEW ===================== */
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewUrl, setPreviewUrl] = useState('');
  const [previewType, setPreviewType] = useState('');
  const [previewTitle, setPreviewTitle] = useState('');
  const [textPreview, setTextPreview] = useState('');

  /* ===================== LOAD LIST ===================== */
  const fetchResources = async () => {
    try {
      setLoading(true);
      const data = await getResources(); // qua API Gateway
      setResources(data);
    } catch {
      message.error('Không thể tải danh sách resource');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchResources();
  }, []);

  /* ===================== UPLOAD ===================== */
  const handleUpload = async () => {
    if (!file) {
      message.warning('Vui lòng chọn file');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      setUploading(true);
      await uploadResource(formData);
      message.success('Upload thành công');
      setFile(null);
      fetchResources();
    } catch {
      message.error('Upload thất bại');
    } finally {
      setUploading(false);
    }
  };

  /* ===================== DELETE ===================== */
  const handleDelete = async (id) => {
    try {
      await deleteResource(id);
      message.success('Đã xóa resource');
      fetchResources();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  /* ===================== PREVIEW ===================== */
  const handlePreview = async (record) => {
    try {
      const blob = await downloadResource(record.id);
      const url = window.URL.createObjectURL(blob);

      setPreviewUrl(url);
      setPreviewType(record.contentType);
      setPreviewTitle(record.name);
      setTextPreview('');

      // TXT
      if (record.contentType === 'text/plain') {
        const text = await blob.text();
        setTextPreview(text);
      }

      setPreviewOpen(true);
    } catch {
      message.error('Không thể xem file');
    }
  };

  const closePreview = () => {
    setPreviewOpen(false);
    setTextPreview('');
    if (previewUrl) {
      window.URL.revokeObjectURL(previewUrl);
    }
  };

  /* ===================== DOWNLOAD ===================== */
  const handleDownload = async (record) => {
    try {
      const blob = await downloadResource(record.id);
      const url = window.URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = record.name;
      a.click();

      window.URL.revokeObjectURL(url);
    } catch {
      message.error('Download thất bại');
    }
  };

  /* ===================== FILTER ===================== */
  const filteredResources = useMemo(() => {
    if (!keyword) return resources;

    return resources.filter(
      (r) =>
        r.name?.toLowerCase().includes(keyword.toLowerCase()) ||
        r.contentType?.toLowerCase().includes(keyword.toLowerCase())
    );
  }, [keyword, resources]);

  /* ===================== TABLE ===================== */
  const columns = [
    {
      title: 'Tên file',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Loại',
      dataIndex: 'contentType',
      key: 'contentType',
      render: (type) => <Tag>{type}</Tag>,
    },
    {
      title: 'Hành động',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button icon={<EyeOutlined />} onClick={() => handlePreview(record)}>
            Xem
          </Button>

          <Button
            icon={<DownloadOutlined />}
            onClick={() => handleDownload(record)}
          >
            Tải
          </Button>

          <Popconfirm
            title="Bạn chắc chắn muốn xóa?"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button danger icon={<DeleteOutlined />}>
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>📁 Resource Service</h2>

      {/* ===================== UPLOAD ===================== */}
      <Space direction="vertical" size="middle" style={{ marginBottom: 24 }}>
        <Upload
          beforeUpload={(f) => {
            setFile(f);
            return false;
          }}
          maxCount={1}
          fileList={file ? [file] : []}
          onRemove={() => setFile(null)}
        >
          <Button icon={<UploadOutlined />}>Chọn file</Button>
        </Upload>

        <Button
          type="primary"
          loading={uploading}
          onClick={handleUpload}
          disabled={!file}
        >
          Upload
        </Button>
      </Space>

      {/* ===================== SEARCH + RELOAD ===================== */}
      <Space style={{ marginBottom: 16 }}>
        <Search
          placeholder="Tìm theo tên hoặc loại file"
          allowClear
          enterButton={<SearchOutlined />}
          onSearch={setKeyword}
          style={{ width: 300 }}
        />

        <Button icon={<ReloadOutlined />} onClick={fetchResources}>
          Reload
        </Button>
      </Space>

      {/* ===================== TABLE ===================== */}
      {loading ? (
        <Spin />
      ) : (
        <Table
          rowKey="id"
          columns={columns}
          dataSource={filteredResources}
          pagination={{ pageSize: 5 }}
        />
      )}

      {/* ===================== PREVIEW MODAL ===================== */}
      <Modal
        open={previewOpen}
        title={previewTitle}
        footer={null}
        width={900}
        onCancel={closePreview}
      >
        {/* IMAGE */}
        {previewType?.startsWith('image') && (
          <img src={previewUrl} alt="preview" style={{ width: '100%' }} />
        )}

        {/* PDF */}
        {previewType === 'application/pdf' && (
          <iframe
            src={previewUrl}
            title="pdf-preview"
            style={{ width: '100%', height: 600 }}
          />
        )}

        {/* TXT */}
        {previewType === 'text/plain' && (
          <pre style={{ maxHeight: 600, overflow: 'auto' }}>
            {textPreview}
          </pre>
        )}

        {/* DOCX */}
        {previewType ===
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document' && (
          <iframe
            title="docx-preview"
            style={{ width: '100%', height: 600 }}
            src={`https://docs.google.com/gview?url=${encodeURIComponent(
              previewUrl
            )}&embedded=true`}
          />
        )}

        {/* UNSUPPORTED */}
        {!previewType?.startsWith('image') &&
          previewType !== 'application/pdf' &&
          previewType !== 'text/plain' &&
          !previewType?.includes('wordprocessingml') && (
            <p>Không hỗ trợ preview file này</p>
          )}
      </Modal>
    </div>
  );
};

export default ResourcePage;
