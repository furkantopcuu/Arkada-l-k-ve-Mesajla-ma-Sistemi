package com.socialplatform.util;

import com.socialplatform.model.Message;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class MessageLogger {
    private static final String LOG_FILE = "mesaj_kayitlari.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void logMessage(Message message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(String.format("[%s] Gönderen: %s, Alıcı: %s, Mesaj: %s", 
                message.getTimestamp().format(FORMATTER),
                message.getSender().getUsername(),
                message.getReceiver().getUsername(),
                message.getContent()));
        } catch (IOException e) {
            System.err.println("Mesaj loglanırken hata oluştu: " + e.getMessage());
        }
    }
    
    public String getLogFilePath() {
        return LOG_FILE;
    }
} 