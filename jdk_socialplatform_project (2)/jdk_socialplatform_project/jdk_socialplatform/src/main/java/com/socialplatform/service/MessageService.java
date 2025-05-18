package com.socialplatform.service;

import com.socialplatform.exception.MessageNotFoundException;
import com.socialplatform.model.Conversation;
import com.socialplatform.model.Message;
import com.socialplatform.model.User;
import com.socialplatform.repository.ConversationRepository;
import com.socialplatform.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final EmailService emailService;
    private final LoggingService loggingService;
    
    public Message sendMessage(User sender, User receiver, String content) {
        // Kullanıcıların arkadaş olup olmadığını kontrol et
        if (!sender.getFriends().contains(receiver)) {
            loggingService.logWarning(String.format(
                "Kullanıcı %s arkadaş olmayan %s kullanıcısına mesaj göndermeye çalıştı", 
                sender.getUsername(), receiver.getUsername()));
            throw new IllegalArgumentException("Arkadaş olmayan kullanıcıya mesaj gönderilemez");
        }
        
        // Mesajı oluştur
        Message message = new Message(sender, receiver, content);
        
        // Konuşmayı bul veya oluştur
        Conversation conversation = conversationRepository
                .findConversationBetween(sender, receiver)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation(sender, receiver);
                    return conversationRepository.save(newConversation);
                });
        
        // Mesajı konuşmaya ekle ve kaydet
        conversation.addMessage(message);
        message = messageRepository.save(message);
        
        // E-posta bildirimi gönder
        emailService.sendNewMessageNotification(sender, receiver);
        
        // Log kaydı oluştur
        loggingService.logInfo(String.format(
            "Mesaj gönderildi: %s -> %s, içerik: %s", 
            sender.getUsername(), receiver.getUsername(), content));
        
        return message;
    }
    
    public List<Message> getMessagesBetween(User user1, User user2) {
        return messageRepository.findBySenderAndReceiver(user1, user2);
    }
    
    public Message markMessageAsRead(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Mesaj bulunamadı: " + messageId));
        
        message.markAsRead();
        return messageRepository.save(message);
    }
    
    public List<Message> getUnreadMessages(User user) {
        return messageRepository.findByReceiverAndReadFalse(user);
    }
    
    public Conversation getOrCreateConversation(User user1, User user2) {
        return conversationRepository
                .findConversationBetween(user1, user2)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation(user1, user2);
                    return conversationRepository.save(newConversation);
                });
    }
    
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUser1OrUser2(user, user);
    }
    
    public Conversation getConversationById(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Konuşma bulunamadı: " + id));
    }
} 