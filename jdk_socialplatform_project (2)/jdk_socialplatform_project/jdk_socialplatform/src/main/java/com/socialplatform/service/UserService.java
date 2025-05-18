package com.socialplatform.service;

import com.socialplatform.exception.UserAlreadyExistsException;
import com.socialplatform.exception.UserNotFoundException;
import com.socialplatform.model.User;
import com.socialplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public User createUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Bu e-posta adresi zaten kullanılıyor: " + email);
        }
        
        User user = new User(username, email, password);
        return userRepository.save(user);
    }
    
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + id));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Bu e-posta ile kayıtlı kullanıcı bulunamadı: " + email));
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void addFriend(User user, User friend) {
        user.addFriend(friend);
        friend.addFriend(user);
        
        userRepository.save(user);
        userRepository.save(friend);
        
        // E-posta bildirimi gönder
        emailService.sendFriendRequestEmail(user, friend);
    }
    
    public void removeFriend(User user, User friend) {
        user.removeFriend(friend);
        friend.removeFriend(user);
        
        userRepository.save(user);
        userRepository.save(friend);
    }
    
    public List<User> getFriends(User user) {
        User refreshedUser = getUserById(user.getId());
        return refreshedUser.getFriends();
    }
} 