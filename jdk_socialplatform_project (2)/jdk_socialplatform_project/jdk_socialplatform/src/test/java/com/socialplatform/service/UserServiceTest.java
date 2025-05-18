package com.socialplatform.service;

import com.socialplatform.exception.UserAlreadyExistsException;
import com.socialplatform.exception.UserNotFoundException;
import com.socialplatform.model.User;
import com.socialplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testFriend;
    private final UUID userId = UUID.randomUUID();
    private final UUID friendId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "test@example.com", "password123");
        testUser.setId(userId);

        testFriend = new User("testFriend", "friend@example.com", "password456");
        testFriend.setId(friendId);
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser("testUser", "test@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> 
            userService.createUser("testUser", "test@example.com", "password123")
        );
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            userService.getUserById(UUID.randomUUID())
        );
    }

    @Test
    void getUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserByEmail_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> 
            userService.getUserByEmail("nonexistent@example.com")
        );
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> userList = Arrays.asList(testUser, testFriend);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(testFriend));
    }

    @Test
    void addFriend_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser).thenReturn(testFriend);

        // Act
        userService.addFriend(testUser, testFriend);

        // Assert
        verify(userRepository).save(testUser);
        verify(userRepository).save(testFriend);
        verify(emailService).sendFriendRequestEmail(testUser, testFriend);
    }

    @Test
    void removeFriend_Success() {
        // Arrange
        testUser.addFriend(testFriend);
        testFriend.addFriend(testUser);
        
        // Act
        userService.removeFriend(testUser, testFriend);
        
        // Assert
        assertFalse(testUser.getFriends().contains(testFriend));
        assertFalse(testFriend.getFriends().contains(testUser));
        verify(userRepository).save(testUser);
        verify(userRepository).save(testFriend);
    }

    @Test
    void getFriends_Success() {
        // Arrange
        testUser.addFriend(testFriend);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Act
        List<User> friends = userService.getFriends(testUser);
        
        // Assert
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertTrue(friends.contains(testFriend));
    }
} 