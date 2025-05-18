package com.socialplatform.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.socialplatform.model.Conversation;
import com.socialplatform.model.Message;
import com.socialplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final MessageService messageService;
    private final LoggingService loggingService;
    
    public String generateConversationPdfReport(UUID conversationId, String filePath) {
        Conversation conversation = messageService.getConversationById(conversationId);
        User user1 = conversation.getUser1();
        User user2 = conversation.getUser2();
        
        if (filePath == null || filePath.isEmpty()) {
            filePath = user1.getUsername() + "_" + user2.getUsername() + "_conversation.pdf";
        }
        
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Başlık
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("Konuşma Raporu", titleFont));
            document.add(new Paragraph("\n"));
            
            // Kullanıcı bilgileri
            document.add(new Paragraph("Kullanıcılar: " + user1.getUsername() + " ve " + user2.getUsername()));
            document.add(new Paragraph("Konuşma ID: " + conversation.getId()));
            document.add(new Paragraph("Tarih: " + java.time.LocalDate.now()));
            document.add(new Paragraph("\n"));
            
            // Mesajlar
            Font messageFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            if (conversation.getMessages().isEmpty()) {
                document.add(new Paragraph("Bu konuşmada henüz mesaj bulunmamaktadır.", messageFont));
            } else {
                for (Message message : conversation.getMessages()) {
                    String senderName = message.getSender().getUsername();
                    String formattedDate = message.getTimestamp().format(FORMATTER);
                    String messageText = message.getContent();
                    
                    document.add(new Paragraph(
                        formattedDate + " - " + senderName + ": " + messageText, messageFont));
                }
            }
            
            document.close();
            loggingService.logInfo("PDF raporu oluşturuldu: " + filePath);
            return filePath;
            
        } catch (DocumentException | IOException e) {
            loggingService.logError("PDF raporu oluşturulurken hata: " + e.getMessage());
            throw new RuntimeException("Rapor oluşturulamadı: " + e.getMessage());
        }
    }
    
    public String generateConversationExcelReport(UUID conversationId, String filePath) {
        Conversation conversation = messageService.getConversationById(conversationId);
        User user1 = conversation.getUser1();
        User user2 = conversation.getUser2();
        
        if (filePath == null || filePath.isEmpty()) {
            filePath = user1.getUsername() + "_" + user2.getUsername() + "_conversation.xlsx";
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Konuşma");
            
            // Başlık satırı
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tarih");
            headerRow.createCell(1).setCellValue("Gönderen");
            headerRow.createCell(2).setCellValue("Mesaj");
            headerRow.createCell(3).setCellValue("Okundu");
            
            // Mesajlar
            int rowIndex = 1;
            for (Message message : conversation.getMessages()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(message.getTimestamp().format(FORMATTER));
                row.createCell(1).setCellValue(message.getSender().getUsername());
                row.createCell(2).setCellValue(message.getContent());
                row.createCell(3).setCellValue(message.isRead() ? "Evet" : "Hayır");
            }
            
            // Sütun genişliklerini otomatik ayarla
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Dosyaya yaz
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            
            loggingService.logInfo("Excel raporu oluşturuldu: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            loggingService.logError("Excel raporu oluşturulurken hata: " + e.getMessage());
            throw new RuntimeException("Rapor oluşturulamadı: " + e.getMessage());
        }
    }
} 