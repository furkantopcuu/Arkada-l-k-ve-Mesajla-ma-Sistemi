package com.socialplatform.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Data
public class MessageRequest {
    
    @NotNull(message = "Gönderen belirtilmelidir")
    private UUID senderId;
    
    @NotNull(message = "Alıcı belirtilmelidir")
    private UUID receiverId;
    
    @NotBlank(message = "Mesaj içeriği boş olamaz")
    @Size(max = 1000, message = "Mesaj en fazla 1000 karakter olabilir")
    private String content;
} 