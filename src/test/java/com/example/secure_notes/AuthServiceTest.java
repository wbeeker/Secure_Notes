package com.example.secure_notes;

import com.example.secure_notes.dto.JwtResponse;
import com.example.secure_notes.dto.LoginRequest;
import com.example.secure_notes.dto.SignupRequest;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import com.example.secure_notes.security.JwtUtil;
import com.example.secure_notes.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("existinguser");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("existinguser");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRoles(Collections.singleton("ROLE_USER"));
    }

    @Test
    @DisplayName("Signup should create new user and return JWT token")
    void testSignupSuccess() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token-123");

        JwtResponse response = authService.signup(signupRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        
        verify(userRepository).findByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user -> 
            user.getUsername().equals("newuser") &&
            user.getPasswordHash().equals("hashedPassword123") &&
            user.getRoles().contains("ROLE_USER")
        ));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Signup should throw exception when username already exists")
    void testSignupUsernameAlreadyTaken() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signup(signupRequest);
        });

        assertEquals("Username is already taken.", exception.getMessage());
        
        verify(userRepository).findByUsername("newuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Signup should save user with encoded password")
    void testSignupPasswordEncoding() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        authService.signup(signupRequest);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user -> 
            user.getPasswordHash().equals("$2a$10$encodedHash")
        ));
    }

    @Test
    @DisplayName("Signup should assign ROLE_USER to new user")
    void testSignupAssignsUserRole() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        authService.signup(signupRequest);

        verify(userRepository).save(argThat(user -> 
            user.getRoles().size() == 1 &&
            user.getRoles().contains("ROLE_USER")
        ));
    }

    @Test
    @DisplayName("Login should authenticate user and return JWT token")
    void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token-456");

        JwtResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token-456", response.getToken());
        
        verify(authenticationManager).authenticate(argThat(auth -> 
            auth.getPrincipal().equals("existinguser") &&
            auth.getCredentials().equals("password123")
        ));
        verify(userRepository).findByUsername("existinguser");
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    @DisplayName("Login should throw exception when user not found")
    void testLoginUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("User not found", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("existinguser");
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Login should throw exception when authentication fails")
    void testLoginAuthenticationFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Login should create correct authentication token")
    void testLoginAuthenticationToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        authService.login(loginRequest);

        verify(authenticationManager).authenticate(argThat(token -> {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) token;
            return authToken.getPrincipal().equals("existinguser") &&
                   authToken.getCredentials().equals("password123");
        }));
    }

    @Test
    @DisplayName("Signup should handle special characters in username")
    void testSignupSpecialCharacters() {
        signupRequest.setUsername("user_123@test");
        when(userRepository.findByUsername("user_123@test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        JwtResponse response = authService.signup(signupRequest);

        assertNotNull(response);
        verify(userRepository).save(argThat(user -> 
            user.getUsername().equals("user_123@test")
        ));
    }

    @Test
    @DisplayName("Signup should handle long passwords")
    void testSignupLongPassword() {
        String longPassword = "a".repeat(100);
        signupRequest.setPassword(longPassword);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(longPassword)).thenReturn("hashedLongPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        authService.signup(signupRequest);

        verify(passwordEncoder).encode(longPassword);
    }

    @Test
    @DisplayName("Login should return different tokens for different users")
    void testLoginDifferentTokensForDifferentUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(jwtUtil.generateToken(user1)).thenReturn("token-for-user1");

        loginRequest.setUsername("user1");

        JwtResponse response = authService.login(loginRequest);

        assertEquals("token-for-user1", response.getToken());
        verify(jwtUtil).generateToken(user1);
    }
}