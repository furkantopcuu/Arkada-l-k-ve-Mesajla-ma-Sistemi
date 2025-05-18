package com.socialplatform.scheduler;

import com.socialplatform.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduled-tasks.enabled", havingValue = "true")
public class MessageCleanupTask {
    
    private final LoggingService loggingService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Her gün gece yarısı çalışır
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldMessages() {
        LocalDateTime now = LocalDateTime.now();
        loggingService.logInfo("Mesaj temizleme görevi çalıştırıldı: " + now.format(FORMATTER));
        
        // Gerçek uygulamada eski mesajları temizleme veya arşivleme işlemi burada yapılır
        // Örnek:
        // messageRepository.deleteOlderThan(now.minusDays(30));
        
        loggingService.logInfo("Mesaj temizleme görevi tamamlandı");
    }
    
    // Her saatte bir çalışır
    @Scheduled(fixedRate = 3600000)
    public void generateMessageStatistics() {
        LocalDateTime now = LocalDateTime.now();
        loggingService.logInfo("Mesaj istatistiği oluşturma görevi çalıştırıldı: " + now.format(FORMATTER));
        
        // Gerçek uygulamada mesaj istatistikleri oluşturma işlemi burada yapılır
        
        loggingService.logInfo("Mesaj istatistiği oluşturma görevi tamamlandı");
    }
} 