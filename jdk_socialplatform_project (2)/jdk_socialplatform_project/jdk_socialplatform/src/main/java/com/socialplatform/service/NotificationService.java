package com.socialplatform.service;

import com.socialplatform.model.User;

public class NotificationService {
    
    public static void sendFriendRequestEmail(User requester, User receiver) {
        // Gerçek bir uygulamada, bu bir e-posta servisine bağlanacaktır
        System.out.println("E-posta gönderiliyor: " + receiver.getEmail());
        System.out.println("Gönderen: " + requester.getUsername());
        System.out.println("E-posta içeriği: " + requester.getUsername() + " kullanıcısından yeni bir arkadaşlık isteği aldınız.");
    }
    
    public static void sendNewMessageNotification(User sender, User receiver) {
        // Gerçek bir uygulamada, bu bir e-posta veya anlık bildirim gönderecektir
        System.out.println("Yeni mesaj bildirimi gönderiliyor: " + receiver.getEmail());
        System.out.println("Gönderen: " + sender.getUsername());
        System.out.println("Bildirim içeriği: " + sender.getUsername() + " kullanıcısından yeni bir mesaj aldınız.");
    }
} 