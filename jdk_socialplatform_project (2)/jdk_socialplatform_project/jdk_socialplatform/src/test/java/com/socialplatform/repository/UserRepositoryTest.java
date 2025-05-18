package com.socialplatform.repository;

import com.socialplatform.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_UserExists_ReturnUser() {
        // Arrange
        User user = new User("testUser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByEmail_UserDoesNotExist_ReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void existsByEmail_UserExists_ReturnTrue() {
        // Arrange
        User user = new User("testUser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_UserDoesNotExist_ReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void saveUser_Success() {
        // Arrange
        User user = new User("newUser", "new@example.com", "password123");

        // Act
        User savedUser = userRepository.save(user);
        entityManager.flush();
        
        // Assert
        assertNotNull(savedUser.getId());
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertNotNull(foundUser);
        assertEquals("newUser", foundUser.getUsername());
        assertEquals("new@example.com", foundUser.getEmail());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        User user = new User("deleteMe", "delete@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        userRepository.delete(user);
        entityManager.flush();
        
        // Assert
        User foundUser = entityManager.find(User.class, user.getId());
        assertNull(foundUser);
    }
} 