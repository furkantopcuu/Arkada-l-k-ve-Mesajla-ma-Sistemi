package com.socialplatform.service;

import com.socialplatform.dto.external.PostDTO;
import com.socialplatform.dto.external.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalApiService {
    
    private final RestTemplate restTemplate;
    private final LoggingService loggingService;
    
    @Value("${app.external-api.url}")
    private String apiBaseUrl;
    
    public List<UserDTO> getExternalUsers() {
        try {
            String url = apiBaseUrl + "/users";
            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UserDTO>>() {}
            );
            
            loggingService.logInfo("Harici API'den kullanıcılar alındı: " + url);
            return response.getBody();
            
        } catch (Exception e) {
            loggingService.logError("Harici API'den kullanıcıları alma hatası: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<PostDTO> getUserPosts(Long userId) {
        try {
            String url = apiBaseUrl + "/users/" + userId + "/posts";
            ResponseEntity<List<PostDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PostDTO>>() {}
            );
            
            loggingService.logInfo("Harici API'den kullanıcı gönderileri alındı: " + url);
            return response.getBody();
            
        } catch (Exception e) {
            loggingService.logError("Harici API'den kullanıcı gönderilerini alma hatası: " + e.getMessage());
            return Collections.emptyList();
        }
    }
} 