package com.socialplatform.api;

import com.socialplatform.dto.external.PostDTO;
import com.socialplatform.dto.external.UserDTO;
import com.socialplatform.service.ExternalApiService;
import com.socialplatform.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {
    
    private final ExternalApiService externalApiService;
    private final LoggingService loggingService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getExternalUsers() {
        loggingService.logInfo("Harici kullanıcılar API isteği alındı");
        return ResponseEntity.ok(externalApiService.getExternalUsers());
    }
    
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable Long userId) {
        loggingService.logInfo("Harici kullanıcı gönderileri API isteği alındı: " + userId);
        return ResponseEntity.ok(externalApiService.getUserPosts(userId));
    }
} 