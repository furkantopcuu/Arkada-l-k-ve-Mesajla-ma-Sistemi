package com.socialplatform.api;

import com.socialplatform.dto.ConversationResponse;
import com.socialplatform.exception.UserNotFoundException;
import com.socialplatform.model.Conversation;
import com.socialplatform.model.User;
import com.socialplatform.service.LoggingService;
import com.socialplatform.service.MessageService;
import com.socialplatform.service.ReportService;
import com.socialplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    
    private final MessageService messageService;
    private final UserService userService;
    private final ReportService reportService;
    private final LoggingService loggingService;
    
    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getUserConversations(@RequestParam UUID userId) {
        try {
            User user = userService.getUserById(userId);
            List<Conversation> conversations = messageService.getUserConversations(user);
            
            List<ConversationResponse> responses = conversations.stream()
                    .map(ConversationResponse::fromConversation)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Konuşma listesi görüntüleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversationById(@PathVariable UUID id) {
        try {
            Conversation conversation = messageService.getConversationById(id);
            return ResponseEntity.ok(ConversationResponse.fromConversation(conversation));
        } catch (Exception e) {
            loggingService.logWarning("Konuşma görüntüleme hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/between")
    public ResponseEntity<ConversationResponse> getConversationBetween(
            @RequestParam UUID user1Id, 
            @RequestParam UUID user2Id) {
        try {
            User user1 = userService.getUserById(user1Id);
            User user2 = userService.getUserById(user2Id);
            
            Conversation conversation = messageService.getOrCreateConversation(user1, user2);
            return ResponseEntity.ok(ConversationResponse.fromConversation(conversation));
            
        } catch (UserNotFoundException e) {
            loggingService.logWarning("Konuşma oluşturma hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{id}/report/pdf")
    public ResponseEntity<Resource> generatePdfReport(@PathVariable UUID id) {
        try {
            String filePath = reportService.generateConversationPdfReport(id, null);
            File file = new File(filePath);
            
            loggingService.logInfo("PDF raporu indirildi: " + filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
            
        } catch (Exception e) {
            loggingService.logError("PDF raporu oluşturma hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/report/excel")
    public ResponseEntity<Resource> generateExcelReport(@PathVariable UUID id) {
        try {
            String filePath = reportService.generateConversationExcelReport(id, null);
            File file = new File(filePath);
            
            loggingService.logInfo("Excel raporu indirildi: " + filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
            
        } catch (Exception e) {
            loggingService.logError("Excel raporu oluşturma hatası: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 