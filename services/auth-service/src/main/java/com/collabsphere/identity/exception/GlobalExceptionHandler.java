package com.collabsphere.identity.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 👈 Annotation này giúp bắt lỗi toàn bộ hệ thống
public class GlobalExceptionHandler {

    // Bắt tất cả các lỗi RuntimeException (như User not found, Unauthenticated...)
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<String> handlingRuntimeException(RuntimeException exception) {
        // Trả về lỗi 400 (Bad Request) thay vì 401/500 để Frontend dễ đọc
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}