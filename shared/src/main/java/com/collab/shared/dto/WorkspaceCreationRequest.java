// File: WorkspaceCreationRequest.java
package com.collab.shared.dto; // (Nhớ đổi package tương ứng cho class-service)

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // <--- 1. BẮT BUỘC THÊM CÁI NÀY (Để Jackson tạo được object rỗng)
@AllArgsConstructor
public class WorkspaceCreationRequest {
    private String teamId;   // Có thể null (nếu là workspace lớp)
    private Long classId;    // Bắt buộc
    private String settingConfig;
}