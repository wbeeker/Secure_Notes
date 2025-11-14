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
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
    }

    @Test
    void findByUsername_test() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals(1L, result.get().getId());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_test2() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void saveUser_test() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPasswordHash("hashedpass");
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.save(newUser);

        assertNotNull(result);
        System.out.println(result.getUsername());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(newUser);
    }

    @Test
    void saveUser_test2() {
        User fullUser = new User();
        fullUser.setUsername("fulluser");
        fullUser.setPasswordHash("hashedpassword");
        
        when(userRepository.save(fullUser)).thenReturn(fullUser);

        User result = userService.save(fullUser);

        assertNotNull(result);
        assertEquals("fulluser", result.getUsername());
        verify(userRepository).save(fullUser);
    }
}
