package com.socialplatform.api;

import com.socialplatform.dto.CreateUserRequest;
import com.socialplatform.dto.UserResponse;
import com.socialplatform.exception.UserAlreadyExistsException;
import com.socialplatform.exception.UserNotFoundException;
import com.socialplatform.model.User;
import com.socialplatform.service.LoggingService;
import com.socialplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final LoggingService loggingService;
    
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
            loggingService.logInfo("Yeni kullanıcı oluşturuldu: " + user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(user));
        } catch (UserAlreadyExistsException e) {
            loggingService.logWarning("Kullanıcı oluşturma hatası: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Kullanıcı bulunamadı: " + id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Kullanıcı bulunamadı (email): " + email);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
        try {
            User user = userService.getUserById(userId);
            User friend = userService.getUserById(friendId);
            
            userService.addFriend(user, friend);
            loggingService.logInfo(String.format(
                "Arkadaşlık eklendi: %s -> %s", user.getUsername(), friend.getUsername()));
            
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Arkadaş ekleme hatası: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            loggingService.logError("Arkadaş ekleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
        try {
            User user = userService.getUserById(userId);
            User friend = userService.getUserById(friendId);
            
            userService.removeFriend(user, friend);
            loggingService.logInfo(String.format(
                "Arkadaşlık silindi: %s -> %s", user.getUsername(), friend.getUsername()));
            
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Arkadaş silme hatası: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserResponse>> getFriends(@PathVariable UUID userId) {
        try {
            User user = userService.getUserById(userId);
            List<User> friends = userService.getFriends(user);
            
            List<UserResponse> responses = friends.stream()
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Arkadaş listesi görüntüleme hatası: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 