package com.collab.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String content;
}
