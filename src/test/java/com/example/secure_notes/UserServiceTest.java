package com.example.secure_notes;

import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import com.example.secure_notes.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser16");
        testUser.setPasswordHash("hashedpassword");
    }

    @Test
    void findByUsername_test() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals(1L, result.get().getId());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_test2() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void saveUser_test() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPasswordHash("hashedpass");
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.save(newUser);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(newUser);
    }

    @Test
    void saveUser_test2() {
        // Given
        User fullUser = new User();
        fullUser.setUsername("fulluser");
        fullUser.setEmail("test@example.com");
        fullUser.setPasswordHash("hashedpassword");
        
        when(userRepository.save(fullUser)).thenReturn(fullUser);

        // When
        User result = userService.save(fullUser);

        // Then
        assertNotNull(result);
        assertEquals("fulluser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(fullUser);
    }


    
}
