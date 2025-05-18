package com.socialplatform.service;

import com.socialplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final LoggingService loggingService;
    
    public void sendFriendRequestEmail(User requester, User receiver) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver.getEmail());
            message.setSubject("Yeni Arkadaşlık İsteği - Sosyal Platform");
            message.setText(requester.getUsername() + " kullanıcısından yeni bir arkadaşlık isteği aldınız." +
                            "\n\nSosyal Platform'u kullandığınız için teşekkür ederiz.");
            
            mailSender.send(message);
            loggingService.logInfo("E-posta gönderildi: " + receiver.getEmail() + ", Gönderen: " + requester.getUsername());
        } catch (Exception e) {
            loggingService.logError("E-posta gönderilirken hata oluştu: " + e.getMessage());
        }
    }
    
    public void sendNewMessageNotification(User sender, User receiver) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver.getEmail());
            message.setSubject("Yeni Mesaj - Sosyal Platform");
            message.setText(sender.getUsername() + " kullanıcısından yeni bir mesaj aldınız." +
                            "\n\nSosyal Platform'u kullandığınız için teşekkür ederiz.");
            
            mailSender.send(message);
            loggingService.logInfo("Mesaj bildirimi gönderildi: " + receiver.getEmail() + ", Gönderen: " + sender.getUsername());
        } catch (Exception e) {
            loggingService.logError("Mesaj bildirimi gönderilirken hata oluştu: " + e.getMessage());
        }
    }
} 