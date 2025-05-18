package com.socialplatform.service;

import com.socialplatform.exception.MessageNotFoundException;
import com.socialplatform.model.Conversation;
import com.socialplatform.model.Message;
import com.socialplatform.model.User;
import com.socialplatform.repository.ConversationRepository;
import com.socialplatform.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Message testMessage;
    private Conversation testConversation;
    private final UUID messageId = UUID.randomUUID();
    private final UUID conversationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sender = new User("sender", "sender@example.com", "password123");
        sender.setId(UUID.randomUUID());

        receiver = new User("receiver", "receiver@example.com", "password456");
        receiver.setId(UUID.randomUUID());

        // Users are friends
        sender.addFriend(receiver);
        receiver.addFriend(sender);

        testMessage = new Message(sender, receiver, "Test message content");
        testMessage.setId(messageId);
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setRead(false);

        testConversation = new Conversation(sender, receiver);
        testConversation.setId(conversationId);
        testConversation.addMessage(testMessage);
    }

    @Test
    void sendMessage_Success() {
        // Arrange
        when(conversationRepository.findConversationBetween(sender, receiver))
                .thenReturn(Optional.of(testConversation));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // Act
        Message result = messageService.sendMessage(sender, receiver, "Test message content");

        // Assert
        assertNotNull(result);
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        assertEquals("Test message content", result.getContent());
        verify(emailService).sendNewMessageNotification(sender, receiver);
        verify(loggingService).logInfo(anyString());
    }

    @Test
    void sendMessage_NotFriends_ThrowsException() {
        // Arrange
        User stranger = new User("stranger", "stranger@example.com", "password789");
        stranger.setId(UUID.randomUUID());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                messageService.sendMessage(sender, stranger, "Test message")
        );
        verify(messageRepository, never()).save(any(Message.class));
        verify(loggingService).logWarning(anyString());
    }

    @Test
    void getMessagesBetween_Success() {
        // Arrange
        List<Message> messages = Arrays.asList(testMessage);
        when(messageRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(messages);

        // Act
        List<Message> result = messageService.getMessagesBetween(sender, receiver);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMessage, result.get(0));
    }

    @Test
    void markMessageAsRead_Success() {
        // Arrange
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // Act
        Message result = messageService.markMessageAsRead(messageId);

        // Assert
        assertTrue(result.isRead());
        verify(messageRepository).save(testMessage);
    }

    @Test
    void markMessageAsRead_MessageNotFound_ThrowsException() {
        // Arrange
        when(messageRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MessageNotFoundException.class, () ->
                messageService.markMessageAsRead(UUID.randomUUID())
        );
    }

    @Test
    void getUnreadMessages_Success() {
        // Arrange
        List<Message> unreadMessages = Arrays.asList(testMessage);
        when(messageRepository.findByReceiverAndReadFalse(receiver)).thenReturn(unreadMessages);

        // Act
        List<Message> result = messageService.getUnreadMessages(receiver);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void getOrCreateConversation_ExistingConversation() {
        // Arrange
        when(conversationRepository.findConversationBetween(sender, receiver))
                .thenReturn(Optional.of(testConversation));

        // Act
        Conversation result = messageService.getOrCreateConversation(sender, receiver);

        // Assert
        assertNotNull(result);
        assertEquals(conversationId, result.getId());
        assertEquals(sender, result.getUser1());
        assertEquals(receiver, result.getUser2());
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void getOrCreateConversation_NewConversation() {
        // Arrange
        when(conversationRepository.findConversationBetween(sender, receiver))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(testConversation);

        // Act
        Conversation result = messageService.getOrCreateConversation(sender, receiver);

        // Assert
        assertNotNull(result);
        assertEquals(testConversation, result);
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void getUserConversations_Success() {
        // Arrange
        List<Conversation> conversations = Arrays.asList(testConversation);
        when(conversationRepository.findByUser1OrUser2(sender, sender)).thenReturn(conversations);

        // Act
        List<Conversation> result = messageService.getUserConversations(sender);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testConversation, result.get(0));
    }

    @Test
    void getConversationById_Success() {
        // Arrange
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(testConversation));

        // Act
        Conversation result = messageService.getConversationById(conversationId);

        // Assert
        assertNotNull(result);
        assertEquals(conversationId, result.getId());
    }

    @Test
    void getConversationById_NotFound_ThrowsException() {
        // Arrange
        when(conversationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                messageService.getConversationById(UUID.randomUUID())
        );
    }
} 