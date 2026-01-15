package com.collab.resourceservice.service;

import com.collab.resourceservice.dto.ResourceResponse;
import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    // 1. Hàm upload file
    ResourceResponse uploadFile(MultipartFile file, ResourceScope scope, String scopeId, String uploaderId, UserRole uploaderRole);

    // 2. Hàm download (trả về mảng byte dữ liệu của file)
    byte[] downloadFile(Long resourceId);

    // 3. Hàm lấy thông tin chi tiết của file (để biết tên gốc, loại file...)
    ResourceResponse getResourceById(Long resourceId);

    // 4. Hàm lấy danh sách file theo Scope (Lớp/Nhóm)
    List<ResourceResponse> getResourcesByScope(ResourceScope scope, String scopeId);

    // 5. Hàm xóa file
    void deleteResource(Long resourceId, String userId, UserRole role);
}