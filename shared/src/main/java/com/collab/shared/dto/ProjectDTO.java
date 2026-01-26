package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String projectCode;
    private String title;       // Tên dự án (Khớp với Entity)
    private String description;
    private String status;      // Dùng String cho an toàn khi parse JSON
    private String classId;
    private String ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    // 👇 Helper: Vì bên TeamService bạn hay quen tay gọi .getName()
    // Hàm này sẽ giúp code cũ không bị lỗi, nó tự trả về title
    public String getName() {
        return this.title;
    }
}