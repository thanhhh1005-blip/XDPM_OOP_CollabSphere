import React from 'react';
import ResourceManager from '../../components/ResourceManager'; // Import component quản lý file

const ResourcePage = () => {
    // 💡 Mẹo: Sau này bạn có thể lấy scope và scopeId từ URL 
    // (Ví dụ: /resources/CLASS/SE104) dùng useParams() của react-router-dom
    
    // Hiện tại mình đang để cứng để test trước
    const currentScope = "CLASS"; 
    const currentId = "SE104";

    return (
        <div className="resource-page-container" style={{ padding: '20px' }}>
            <div className="header-section" style={{ marginBottom: '20px' }}>
                <h1 style={{ color: '#2c3e50' }}>Kho Tài Liệu Tập Trung 📚</h1>
                <p style={{ color: '#7f8c8d' }}>
                    Quản lý tài liệu cho {currentScope} - {currentId}
                </p>
            </div>

            {/* Gọi Component ResourceManager vào đây */}
            <div className="content-section" style={{ background: '#fff', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)' }}>
                <ResourceManager 
                    scope={currentScope} 
                    scopeId={currentId} 
                />
            </div>
        </div>
    );
};

export default ResourcePage;