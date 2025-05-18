package com.socialplatform.repository;

import com.socialplatform.model.Conversation;
import com.socialplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c WHERE (c.user1 = ?1 AND c.user2 = ?2) OR (c.user1 = ?2 AND c.user2 = ?1)")
    Optional<Conversation> findConversationBetween(User user1, User user2);
    
    List<Conversation> findByUser1OrUser2(User user1, User user2);
} 