package com.socialplatform.dto;

import com.socialplatform.model.Conversation;
import com.socialplatform.model.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class ConversationResponse {
    private UUID id;
    private UUID user1Id;
    private String user1Username;
    private UUID user2Id;
    private String user2Username;
    private List<MessageResponse> messages;
    
    public static ConversationResponse fromConversation(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .user1Id(conversation.getUser1().getId())
                .user1Username(conversation.getUser1().getUsername())
                .user2Id(conversation.getUser2().getId())
                .user2Username(conversation.getUser2().getUsername())
                .messages(conversation.getMessages().stream()
                        .map(MessageResponse::fromMessage)
                        .collect(Collectors.toList()))
                .build();
    }
} 