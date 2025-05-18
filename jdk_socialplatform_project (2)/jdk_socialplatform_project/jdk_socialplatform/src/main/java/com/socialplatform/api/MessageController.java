package com.socialplatform.api;

import com.socialplatform.dto.MessageRequest;
import com.socialplatform.dto.MessageResponse;
import com.socialplatform.exception.UserNotFoundException;
import com.socialplatform.model.Message;
import com.socialplatform.model.User;
import com.socialplatform.service.LoggingService;
import com.socialplatform.service.MessageService;
import com.socialplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    private final UserService userService;
    private final LoggingService loggingService;
    
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        try {
            User sender = userService.getUserById(request.getSenderId());
            User receiver = userService.getUserById(request.getReceiverId());
            
            Message message = messageService.sendMessage(sender, receiver, request.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.fromMessage(message));
            
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Mesaj gönderme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            loggingService.logWarning("Mesaj gönderme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            loggingService.logError("Mesaj gönderme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/between")
    public ResponseEntity<List<MessageResponse>> getMessagesBetween(
            @RequestParam UUID user1Id, 
            @RequestParam UUID user2Id) {
        try {
            User user1 = userService.getUserById(user1Id);
            User user2 = userService.getUserById(user2Id);
            
            List<Message> messages = messageService.getMessagesBetween(user1, user2);
            List<MessageResponse> responses = messages.stream()
                    .map(MessageResponse::fromMessage)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Mesaj görüntüleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markMessageAsRead(@PathVariable UUID id) {
        try {
            Message message = messageService.markMessageAsRead(id);
            return ResponseEntity.ok(MessageResponse.fromMessage(message));
        } catch (Exception e) {
            loggingService.logWarning("Mesaj okundu işaretleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(@RequestParam UUID userId) {
        try {
            User user = userService.getUserById(userId);
            List<Message> messages = messageService.getUnreadMessages(user);
            
            List<MessageResponse> responses = messages.stream()
                    .map(MessageResponse::fromMessage)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Okunmamış mesaj görüntüleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
} 