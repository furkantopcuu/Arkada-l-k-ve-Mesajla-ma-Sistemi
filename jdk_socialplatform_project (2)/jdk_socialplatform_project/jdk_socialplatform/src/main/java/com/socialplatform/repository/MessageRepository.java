package com.socialplatform.repository;

import com.socialplatform.model.Message;
import com.socialplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySender(User sender);
    List<Message> findByReceiver(User receiver);
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiverAndReadFalse(User receiver);
} 