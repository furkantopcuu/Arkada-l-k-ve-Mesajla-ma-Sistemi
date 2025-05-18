package com.socialplatform.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    @NotNull(message = "Birinci kullanıcı belirtilmelidir")
    private User user1;
    
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    @NotNull(message = "İkinci kullanıcı belirtilmelidir")
    private User user2;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    
    public Conversation(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
    
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }
    
    public boolean involvesUser(User user) {
        return user1.equals(user) || user2.equals(user);
    }
} 