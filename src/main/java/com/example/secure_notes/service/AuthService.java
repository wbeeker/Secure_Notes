package com.example.secure_notes.service;

import com.example.secure_notes.dto.JwtResponse;
import com.example.secure_notes.dto.LoginRequest;
import com.example.secure_notes.dto.SignupRequest;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import com.example.secure_notes.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/*
 * Service class for handling user authentication operations. This service manages the
 * complete authentication lifecycle including user signup and login. 
 */
@Service
public class AuthService {

    /*
     * Repository for user data persistence and retrieval.
     */
    private final UserRepository userRepository;

    /*
     * Password encoder for secure password hashing. Uses BCrypt. 
     */
    private final PasswordEncoder passwordEncoder;

    /*
     * Utility for JWT token operations. 
     */
    private final JwtUtil jwtUtil;

    /*
     * Spring Security authentication manager. 
     */
    private final AuthenticationManager authenticationManager;

    /*
     * Constructs an AuthService with all required dependencies. 
     * 
     * @param userRepository repository for user data operations
     * @param passwordEncoder encoder for password hashing
     * @param jwtUtil utility for JWT token operations
     * @param authenticationManager manager for authentication operations
     */
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /*
     * Registers a new user account and generates an authentication token.
     * 
     * @param request the signup request containing username and password (must not be null)
     * @return JwtResponse containing the authentication token for immediate use
     * @throws RuntimeException if the username is already taken
     * @throws NullPointerException if request or required fields are null
     */
    public JwtResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton("ROLE_USER"));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new JwtResponse(token);
    }

    /*
     * Authenticates a user and generates a JWT token.
     * 
     * @param request the login request containing username and password (must not be null)
     * @return JwtResponse containing the JWT token for authenticated requests
     * @throws BadCredentialsException if username or password is invalid
     * @throws RuntimeException if user is not found after successful authentication (should not occur normally)
     * @throws DisabledException if the account is disabled
     * @throws LockedException if the account is locked
     */
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);
        return new JwtResponse(token);
    }
}
