package com.collab.resourceservice.exception;

import com.collab.resourceservice.dto.ApiResponse;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ... (Các hàm handleBadRequest, handleForbidden... bạn giữ nguyên cũng được)

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        // --- ĐOẠN MỚI THÊM VÀO ---
        System.err.println("============= LỖI NGHIÊM TRỌNG (500) =============");
        ex.printStackTrace(); // <--- DÒNG QUAN TRỌNG NHẤT: In lỗi đỏ ra Terminal
        System.err.println("==================================================");
        // -------------------------

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Internal server error: " + ex.getMessage(), null));
    }
}