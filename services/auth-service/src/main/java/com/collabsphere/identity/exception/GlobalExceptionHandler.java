package com.collabsphere.identity.exception;

import com.collabsphere.identity.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Bắt tất cả các lỗi RuntimeException (như lỗi sai pass, khóa tài khoản...)
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        
        apiResponse.setCode(1001); // Mã lỗi mặc định (hoặc số khác tùy bạn)
        apiResponse.setMessage(exception.getMessage()); // Lấy message bạn đã throw bên Service
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
}