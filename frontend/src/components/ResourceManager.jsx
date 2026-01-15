import React, { useState, useEffect } from 'react';
import ResourceService from '../services/resourceService'; // Import service vừa viết

const ResourceManager = ({ scope, scopeId }) => {
    const [files, setFiles] = useState([]);
    const [loading, setLoading] = useState(false);

    // Load danh sách khi Component hiện lên
    useEffect(() => {
        loadFiles();
    }, [scope, scopeId]);

    const loadFiles = async () => {
        setLoading(true);
        try {
            const data = await ResourceService.getResources(scope, scopeId);
            setFiles(data);
        } catch (error) {
            alert("Không tải được danh sách file!");
        } finally {
            setLoading(false);
        }
    };

    // Xử lý khi chọn file để upload
    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        try {
            await ResourceService.uploadFile(file, scope, scopeId);
            alert("Upload thành công!");
            loadFiles(); // Load lại danh sách ngay lập tức
        } catch (error) {
            alert("Upload thất bại!");
        }
    };

    // Xử lý xóa
    const handleDelete = async (id) => {
        if (!window.confirm("Bạn có chắc muốn xóa file này?")) return;
        try {
            await ResourceService.deleteFile(id);
            setFiles(files.filter(f => f.id !== id)); // Xóa file khỏi giao diện
        } catch (error) {
            alert("Không xóa được (Có thể do không đủ quyền)!");
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
            <h3>📂 Tài liệu chung ({scopeId})</h3>
            
            {/* Nút Upload */}
            <div style={{ marginBottom: '20px' }}>
                <input type="file" onChange={handleFileChange} />
            </div>

            {/* Danh sách file */}
            {loading ? <p>Đang tải...</p> : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ background: '#f5f5f5', textAlign: 'left' }}>
                            <th style={{ padding: '10px' }}>Tên file</th>
                            <th>Người đăng</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        {files.length === 0 ? (
                            <tr><td colSpan="3" style={{textAlign: 'center', padding: '10px'}}>Chưa có tài liệu nào</td></tr>
                        ) : (
                            files.map(file => (
                                <tr key={file.id} style={{ borderBottom: '1px solid #eee' }}>
                                    <td style={{ padding: '10px' }}>{file.fileName}</td>
                                    <td>{file.uploadedBy}</td>
                                    <td>
                                        <button 
                                            onClick={() => ResourceService.downloadFile(file.id, file.fileName)}
                                            style={{ marginRight: '10px', cursor: 'pointer', color: 'blue' }}
                                        >
                                            ⬇ Tải về
                                        </button>
                                        <button 
                                            onClick={() => handleDelete(file.id)}
                                            style={{ cursor: 'pointer', color: 'red' }}
                                        >
                                            ❌ Xóa
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ResourceManager;