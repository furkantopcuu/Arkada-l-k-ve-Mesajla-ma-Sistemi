package com.socialplatform.dto;

import com.socialplatform.model.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private boolean read;
    private LocalDateTime timestamp;
    private UserResponse sender;
    private UserResponse receiver;
    private UUID conversationId;

    public static MessageResponse fromMessage(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setRead(message.isRead());
        response.setTimestamp(message.getTimestamp());
        
        if (message.getSender() != null) {
            response.setSender(UserResponse.fromUser(message.getSender()));
        }
        
        if (message.getReceiver() != null) {
            response.setReceiver(UserResponse.fromUser(message.getReceiver()));
        }
        
        if (message.getConversation() != null) {
            response.setConversationId(message.getConversation().getId());
        }
        
        return response;
    }
} 