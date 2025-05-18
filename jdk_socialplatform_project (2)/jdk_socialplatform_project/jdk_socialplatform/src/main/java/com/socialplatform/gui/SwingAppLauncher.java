package com.socialplatform.gui;

import com.socialplatform.dto.CreateUserRequest;
import com.socialplatform.dto.MessageRequest;
import com.socialplatform.model.User;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Swing tabanlı kullanıcı arayüzü
 */
public class SwingAppLauncher extends JFrame {
    private ConfigurableApplicationContext context;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel, registerPanel, usersPanel, messagesPanel, friendsPanel;
    private JTextField loginEmailField, loginPasswordField;
    private JTextField registerUsernameField, registerEmailField, registerPasswordField;
    private JTextField receiverIdField, messageContentField;
    private JTextArea messagesArea;
    private JTable usersTable, friendsTable;
    private DefaultTableModel userTableModel, friendsTableModel;
    private User currentUser;
    
    private final String BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        // Çalışan Java süreçlerini sonlandır
        try {
            Runtime.getRuntime().exec("pkill -f java");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Süreç sonlandırma hatası: " + e.getMessage());
        }

        // H2 veritabanı dosyalarını temizle
        try {
            Runtime.getRuntime().exec("rm -f socialplatform.mv.db socialplatform.trace.db");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Dosya silme hatası: " + e.getMessage());
        }

        // Spring Boot uygulamasını ve Swing arayüzünü başlat
        SwingUtilities.invokeLater(() -> {
            try {
                SwingAppLauncher app = new SwingAppLauncher();
                app.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Uygulama başlatılamadı: " + e.getMessage());
            }
        });
    }

    public SwingAppLauncher() {
        // Spring Boot uygulamasını başlat
        startSpringBootApp();
        
        // Swing arayüzünü hazırla
        setupUI();
    }

    private void startSpringBootApp() {
        // Spring Boot uygulamasını arka planda başlat
        new Thread(() -> {
            try {
                String[] args = new String[]{"--server.port=8080", "--spring.datasource.url=jdbc:h2:mem:socialplatform"};
                this.context = new SpringApplicationBuilder(com.socialplatform.SocialPlatformApplication.class)
                        .headless(false)
                        .run(args);
                System.out.println("Spring Boot uygulaması başlatıldı!");
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, "Spring Boot uygulaması başlatılamadı: " + e.getMessage())
                );
            }
        }).start();
        
        // Spring Boot'un başlaması için biraz bekle
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        setTitle("Sosyal Platform - Swing Arayüzü");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Tab paneli oluştur
        tabbedPane = new JTabbedPane();
        
        // Login paneli
        loginPanel = createLoginPanel();
        tabbedPane.addTab("Giriş", loginPanel);
        
        // Kayıt paneli
        registerPanel = createRegisterPanel();
        tabbedPane.addTab("Kayıt Ol", registerPanel);
        
        // Kullanıcılar paneli
        usersPanel = createUsersPanel();
        tabbedPane.addTab("Kullanıcılar", usersPanel);
        
        // Arkadaşlar paneli
        friendsPanel = createFriendsPanel();
        tabbedPane.addTab("Arkadaşlarım", friendsPanel);
        
        // Mesajlar paneli
        messagesPanel = createMessagesPanel();
        tabbedPane.addTab("Mesajlar", messagesPanel);
        
        // Tab panelini ekran boyutuna genişlet
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel emailLabel = new JLabel("E-posta:");
        loginEmailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Şifre:");
        loginPasswordField = new JPasswordField(20);
        
        JButton loginButton = new JButton("Giriş Yap");
        loginButton.addActionListener(e -> login());
        
        formPanel.add(emailLabel);
        formPanel.add(loginEmailField);
        formPanel.add(passwordLabel);
        formPanel.add(loginPasswordField);
        formPanel.add(new JLabel(""));
        formPanel.add(loginButton);
        
        panel.add(new JLabel("Sosyal Platform - Giriş", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        registerUsernameField = new JTextField(20);
        JLabel emailLabel = new JLabel("E-posta:");
        registerEmailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Şifre:");
        registerPasswordField = new JPasswordField(20);
        
        JButton registerButton = new JButton("Kayıt Ol");
        registerButton.addActionListener(e -> register());
        
        formPanel.add(usernameLabel);
        formPanel.add(registerUsernameField);
        formPanel.add(emailLabel);
        formPanel.add(registerEmailField);
        formPanel.add(passwordLabel);
        formPanel.add(registerPasswordField);
        formPanel.add(new JLabel(""));
        formPanel.add(registerButton);
        
        JLabel noteLabel = new JLabel("<html>Not: Kullanıcı adı 3-50 karakter arasında olmalıdır.<br>" +
                "Şifre en az 6 karakter ve en az bir rakam ve bir harf içermelidir.</html>");
        noteLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        
        panel.add(new JLabel("Sosyal Platform - Kayıt Ol", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(noteLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID", "Kullanıcı Adı", "E-posta"};
        userTableModel = new DefaultTableModel(columnNames, 0);
        usersTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton refreshButton = new JButton("Kullanıcıları Yenile");
        refreshButton.addActionListener(e -> loadUsers());
        
        JButton addFriendButton = new JButton("Arkadaş Ekle");
        addFriendButton.addActionListener(e -> addFriend());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addFriendButton);
        
        panel.add(new JLabel("Kullanıcılar", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFriendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID", "Kullanıcı Adı", "E-posta"};
        friendsTableModel = new DefaultTableModel(columnNames, 0);
        friendsTable = new JTable(friendsTableModel);
        JScrollPane scrollPane = new JScrollPane(friendsTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton refreshButton = new JButton("Arkadaş Listesini Yenile");
        refreshButton.addActionListener(e -> loadFriendsList());
        
        JButton removeFriendButton = new JButton("Arkadaşlıktan Çıkar");
        removeFriendButton.addActionListener(e -> removeFriend());
        
        JButton sendMessageButton = new JButton("Mesaj Gönder");
        sendMessageButton.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow != -1) {
                String friendId = (String) friendsTableModel.getValueAt(selectedRow, 0);
                receiverIdField.setText(friendId);
                tabbedPane.setSelectedIndex(4); // Mesajlar sekmesine git
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen mesaj göndermek için bir arkadaş seçin.");
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(removeFriendButton);
        buttonPanel.add(sendMessageButton);
        
        panel.add(new JLabel("Arkadaşlarım", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        
        JPanel sendPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        sendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel receiverLabel = new JLabel("Alıcı ID:");
        receiverIdField = new JTextField(36);
        JLabel contentLabel = new JLabel("Mesaj:");
        messageContentField = new JTextField(36);
        
        JButton sendButton = new JButton("Mesaj Gönder");
        sendButton.addActionListener(e -> sendMessage());
        
        JButton refreshButton = new JButton("Mesajları Yenile");
        refreshButton.addActionListener(e -> loadMessages());
        
        sendPanel.add(receiverLabel);
        sendPanel.add(receiverIdField);
        sendPanel.add(contentLabel);
        sendPanel.add(messageContentField);
        sendPanel.add(sendButton);
        sendPanel.add(refreshButton);
        
        panel.add(new JLabel("Mesajlar", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(sendPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void login() {
        try {
            String email = loginEmailField.getText();
            String password = loginPasswordField.getText();
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "E-posta ve şifre alanlarını doldurunuz.");
                return;
            }
            
            // E-posta formatını kontrol et
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Geçerli bir e-posta adresi giriniz. Örnek: kullanici@example.com");
                return;
            }
            
            // Kullanıcıyı e-posta adresine göre getir
            try {
                String url = BASE_URL + "/users/email/" + email;
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                
                if (!response.getStatusCode().is2xxSuccessful()) {
                    JOptionPane.showMessageDialog(this, "Kullanıcı bulunamadı!");
                    return;
                }
                
                Map<String, Object> userData = response.getBody();
                currentUser = new User();
                currentUser.setId(UUID.fromString((String) userData.get("id")));
                currentUser.setUsername((String) userData.get("username"));
                currentUser.setEmail((String) userData.get("email"));
                
                JOptionPane.showMessageDialog(this, "Giriş başarılı!");
                
                // Kullanıcılar sekmesine geç ve kullanıcıları yükle
                tabbedPane.setSelectedIndex(2); // Kullanıcılar sekmesi
                loadUsers();
                loadFriendsList(); // Arkadaş listesini de yükle
            } catch (HttpClientErrorException.NotFound e) {
                JOptionPane.showMessageDialog(this, "Bu e-posta adresiyle kayıtlı kullanıcı bulunamadı: " + email);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giriş hatası: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giriş hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void register() {
        try {
            String username = registerUsernameField.getText();
            String email = registerEmailField.getText();
            String password = registerPasswordField.getText();
            
            // Basit doğrulama
            if (username.length() < 3 || username.length() > 50) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı 3-50 karakter arasında olmalıdır.");
                return;
            }
            
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Geçerli bir e-posta adresi giriniz.");
                return;
            }
            
            if (!password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$")) {
                JOptionPane.showMessageDialog(this, "Şifre en az 6 karakter uzunluğunda olmalı ve en az bir rakam ve bir harf içermelidir.");
                return;
            }
            
            // Kullanıcı kaydı için istek gönder
            CreateUserRequest request = new CreateUserRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            
            String url = BASE_URL + "/users";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this, "Kayıt başarılı! Şimdi giriş yapabilirsiniz.");
                
                // Kayıt alanlarını temizle
                registerUsernameField.setText("");
                registerEmailField.setText("");
                registerPasswordField.setText("");
                
                // Giriş sekmesine geç
                tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Kayıt işlemi başarısız oldu!");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kayıt hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadUsers() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            String url = BASE_URL + "/users";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this, "Kullanıcılar yüklenemedi!");
                return;
            }
            
            List<Map<String, Object>> users = response.getBody();
            
            // Tablo modelini temizle
            userTableModel.setRowCount(0);
            
            // Kullanıcıları tabloya ekle
            for (Map<String, Object> user : users) {
                String id = (String) user.get("id");
                String username = (String) user.get("username");
                String email = (String) user.get("email");
                
                userTableModel.addRow(new Object[]{id, username, email});
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kullanıcılar yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addFriend() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen arkadaş eklemek için bir kullanıcı seçin.");
                return;
            }
            
            String friendId = (String) userTableModel.getValueAt(selectedRow, 0);
            String friendUsername = (String) userTableModel.getValueAt(selectedRow, 1);
            
            // Kendisini arkadaş olarak eklemeye çalışıyorsa engelle
            if (friendId.equals(currentUser.getId().toString())) {
                JOptionPane.showMessageDialog(this, "Kendinizi arkadaş olarak ekleyemezsiniz!");
                return;
            }
            
            // Kullanıcının arkadaş listesini kontrol et
            String checkFriendsUrl = BASE_URL + "/users/" + currentUser.getId() + "/friends";
            ResponseEntity<List> friendsResponse = restTemplate.getForEntity(checkFriendsUrl, List.class);
            
            if (friendsResponse.getStatusCode().is2xxSuccessful() && friendsResponse.getBody() != null) {
                List<Map<String, Object>> friends = friendsResponse.getBody();
                
                for (Map<String, Object> friend : friends) {
                    String currentFriendId = (String) friend.get("id");
                    if (currentFriendId != null && currentFriendId.equals(friendId)) {
                        JOptionPane.showMessageDialog(this, friendUsername + " zaten arkadaş listenizde!");
                        return;
                    }
                }
            }
            
            // Arkadaş ekleme isteği gönder
            String addFriendUrl = BASE_URL + "/users/" + currentUser.getId() + "/friends/" + friendId;
            restTemplate.postForEntity(addFriendUrl, null, Void.class);
            
            JOptionPane.showMessageDialog(this, friendUsername + " arkadaş olarak eklendi!");
            loadFriendsList(); // Arkadaş listesini yenile
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Arkadaş ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadFriendsList() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            String url = BASE_URL + "/users/" + currentUser.getId() + "/friends";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this, "Arkadaş listesi yüklenemedi!");
                return;
            }
            
            List<Map<String, Object>> friends = response.getBody();
            
            // Tablo modelini temizle
            friendsTableModel.setRowCount(0);
            
            // Arkadaşları tabloya ekle
            for (Map<String, Object> friend : friends) {
                String id = (String) friend.get("id");
                String username = (String) friend.get("username");
                String email = (String) friend.get("email");
                
                friendsTableModel.addRow(new Object[]{id, username, email});
            }
            
            if (friends.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Arkadaş listeniz boş. Kullanıcılar sekmesinden arkadaş ekleyebilirsiniz.");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Arkadaş listesi yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void removeFriend() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen listeden çıkarmak için bir arkadaş seçin.");
                return;
            }
            
            String friendId = (String) friendsTableModel.getValueAt(selectedRow, 0);
            String friendUsername = (String) friendsTableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                friendUsername + " adlı kullanıcıyı arkadaş listenizden çıkarmak istediğinizden emin misiniz?",
                "Arkadaşı Kaldır",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                String url = BASE_URL + "/users/" + currentUser.getId() + "/friends/" + friendId;
                restTemplate.delete(url);
                
                JOptionPane.showMessageDialog(this, friendUsername + " arkadaş listenizden çıkarıldı.");
                loadFriendsList(); // Arkadaş listesini yenile
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Arkadaş silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadMessages() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            String url = BASE_URL + "/messages/unread?userId=" + currentUser.getId();
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this, "Mesajlar yüklenemedi!");
                return;
            }
            
            List<Map<String, Object>> messages = response.getBody();
            
            // Mesaj alanını temizle
            messagesArea.setText("");
            
            // Mesajları ekle
            if (messages.isEmpty()) {
                messagesArea.append("Okunmamış mesajınız yok.\n");
            } else {
                messagesArea.append("Okunmamış Mesajlar:\n\n");
                
                for (Map<String, Object> message : messages) {
                    Map<String, Object> sender = (Map<String, Object>) message.get("sender");
                    String senderUsername = (String) sender.get("username");
                    String content = (String) message.get("content");
                    String timestamp = (String) message.get("timestamp");
                    
                    messagesArea.append("Gönderen: " + senderUsername + "\n");
                    messagesArea.append("İçerik: " + content + "\n");
                    messagesArea.append("Tarih: " + timestamp + "\n");
                    messagesArea.append("-----------------------\n");
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Mesajlar yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendMessage() {
        try {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Lütfen önce giriş yapın.");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
                return;
            }
            
            String receiverId = receiverIdField.getText();
            String content = messageContentField.getText();
            
            if (receiverId.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alıcı ID ve mesaj içeriği alanlarını doldurunuz.");
                return;
            }
            
            // Alıcının arkadaş listesinde olup olmadığını kontrol et
            String friendsUrl = BASE_URL + "/users/" + currentUser.getId() + "/friends";
            ResponseEntity<List> friendsResponse = restTemplate.getForEntity(friendsUrl, List.class);
            
            if (friendsResponse.getStatusCode().is2xxSuccessful() && friendsResponse.getBody() != null) {
                List<Map<String, Object>> friends = friendsResponse.getBody();
                boolean isFriend = false;
                
                for (Map<String, Object> friend : friends) {
                    String friendId = (String) friend.get("id");
                    if (friendId != null && friendId.equals(receiverId)) {
                        isFriend = true;
                        break;
                    }
                }
                
                if (!isFriend) {
                    JOptionPane.showMessageDialog(this, "Bu kullanıcı arkadaş listenizde değil. Mesaj göndermek için önce arkadaş eklemelisiniz.");
                    return;
                }
            }
            
            // Mesaj gönderme isteği
            MessageRequest request = new MessageRequest();
            request.setSenderId(currentUser.getId());
            request.setReceiverId(UUID.fromString(receiverId));
            request.setContent(content);
            
            String url = BASE_URL + "/messages";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this, "Mesaj başarıyla gönderildi!");
                
                // Mesaj içeriği alanını temizle
                messageContentField.setText("");
                
                // Mesajları yenile
                loadMessages();
            } else {
                JOptionPane.showMessageDialog(this, "Mesaj gönderilemedi!");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Mesaj gönderme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Spring Boot uygulamasını durdur
    @Override
    public void dispose() {
        if (context != null) {
            context.close();
        }
        super.dispose();
    }
} 