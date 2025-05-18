package com.socialplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class LoggingService {
    
    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void logInfo(String message) {
        log.info(message);
        writeToLogFile("INFO", message);
    }
    
    public void logError(String message) {
        log.error(message);
        writeToLogFile("ERROR", message);
    }
    
    public void logWarning(String message) {
        log.warn(message);
        writeToLogFile("WARNING", message);
    }
    
    private void writeToLogFile(String level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(String.format("[%s] [%s] %s", 
                LocalDateTime.now().format(FORMATTER),
                level,
                message));
        } catch (IOException e) {
            log.error("Log dosyasına yazılırken hata oluştu: " + e.getMessage());
        }
    }
} 