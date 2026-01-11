package com.collab.resourceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

<<<<<<< HEAD
    public static ApiResponse<?> error(String message) {
=======
    public static <T> ApiResponse<T> error(String message) {
>>>>>>> origin/main
        return new ApiResponse<>(false, message, null);
    }
}
