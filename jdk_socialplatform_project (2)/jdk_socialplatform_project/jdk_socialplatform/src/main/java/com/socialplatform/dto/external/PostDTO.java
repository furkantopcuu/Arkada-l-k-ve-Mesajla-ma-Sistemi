package com.socialplatform.dto.external;

import lombok.Data;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String body;
} 