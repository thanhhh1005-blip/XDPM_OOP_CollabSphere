package com.collab.projectservice.api;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectReq(
    @NotBlank(message = "Tiêu đề không được để trống")
    String title,

    @NotBlank(message = "Mô tả không được để trống")
    String description,
    
    @NotBlank(message = "Nội dung đề cương không được để trống")
    String syllabusContent
) {}
