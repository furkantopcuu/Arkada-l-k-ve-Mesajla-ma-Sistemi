package com.socialplatform.dto;

import com.socialplatform.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private List<UUID> friendIds;
    
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .friendIds(user.getFriends().stream()
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .build();
    }
} 