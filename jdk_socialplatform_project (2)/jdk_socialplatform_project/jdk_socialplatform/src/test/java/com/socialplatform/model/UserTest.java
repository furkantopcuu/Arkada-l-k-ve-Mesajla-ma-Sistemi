package com.socialplatform.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User testUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "test@example.com", "password123");
        testUser.setId(UUID.randomUUID());

        friendUser = new User("friendUser", "friend@example.com", "password456");
        friendUser.setId(UUID.randomUUID());
    }

    @Test
    void createUser_Success() {
        // Assert basic properties
        assertEquals("testUser", testUser.getUsername());
        assertEquals("test@example.com", testUser.getEmail());
        assertEquals("password123", testUser.getPassword());
        assertNotNull(testUser.getId());
        assertTrue(testUser.getFriends().isEmpty());
        assertTrue(testUser.getConversations().isEmpty());
    }

    @Test
    void addFriend_Success() {
        // Act
        testUser.addFriend(friendUser);
        
        // Assert
        assertEquals(1, testUser.getFriends().size());
        assertTrue(testUser.getFriends().contains(friendUser));
    }

    @Test
    void addFriend_AlreadyFriend_ShouldNotAddDuplicate() {
        // Arrange
        testUser.addFriend(friendUser);
        int initialFriendCount = testUser.getFriends().size();
        
        // Act - try to add the same friend again
        testUser.addFriend(friendUser);
        
        // Assert - should still only have one friend
        assertEquals(initialFriendCount, testUser.getFriends().size());
        assertEquals(1, testUser.getFriends().size());
    }

    @Test
    void removeFriend_Success() {
        // Arrange
        testUser.addFriend(friendUser);
        assertTrue(testUser.getFriends().contains(friendUser));
        
        // Act
        testUser.removeFriend(friendUser);
        
        // Assert
        assertFalse(testUser.getFriends().contains(friendUser));
        assertEquals(0, testUser.getFriends().size());
    }

    @Test
    void removeFriend_NotFriend_NoEffect() {
        // Arrange
        User strangerUser = new User("stranger", "stranger@example.com", "password789");
        strangerUser.setId(UUID.randomUUID());
        
        // Act
        testUser.removeFriend(strangerUser);
        
        // Assert - nothing should change
        assertEquals(0, testUser.getFriends().size());
    }

    @Test
    void equalsAndHashCode_SameId_Equal() {
        // Arrange
        UUID sharedId = UUID.randomUUID();
        
        User user1 = new User("user1", "user1@example.com", "password");
        user1.setId(sharedId);
        
        User user2 = new User("user2", "user2@example.com", "different_password");
        user2.setId(sharedId);
        
        // Assert - despite different names/emails, should be equal due to same ID
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalsAndHashCode_DifferentId_NotEqual() {
        // Assert - should not be equal since they have different IDs
        assertNotEquals(testUser, friendUser);
        assertNotEquals(testUser.hashCode(), friendUser.hashCode());
    }
} 