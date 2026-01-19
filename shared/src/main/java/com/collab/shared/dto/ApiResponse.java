package com.collab.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder // 👈 QUAN TRỌNG: Thêm cái này để dùng được .builder()
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 👈 Giúp ẩn các trường null (cho gọn JSON)
public class ApiResponse<T> {
    
    @Builder.Default
    private int code = 1000; // 👈 Đổi 'success' thành 'code' để khớp với Controller (mặc định 1000 là thành công)
    
    private String message;
    
    private T result; // 👈 Đổi 'data' thành 'result' cho chuẩn (hoặc giữ là 'data' tùy bạn, nhưng nhớ sửa Controller)
}