package com.example.secure_notes.controller;

import com.example.secure_notes.dto.LoginRequest;
import com.example.secure_notes.dto.SignupRequest;
import com.example.secure_notes.dto.JwtResponse;
import com.example.secure_notes.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/*
 * REST controller for handling authentication endpoints.
 * 
 * Provides HTTP endpoints for user authentication operations
 * including user registration and login. Serves as an entry point
 * for authentication requests. 
 */
@RestController
@RequestMapping("/api/auth")
@Component
public class AuthController {

    /*
     * Service layer dependency for authentication business logic.
     */
    private final AuthService authService;

    /*
     * Constructs an AuthController with required dependencies.
     * 
     * @param authService the authentication service for business logic
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * Registers a new user account and returns a JWT token.
     * 
     * @param signupRequest the registration request containing username and password
     * @return ResponseEntity with HTTP 200 OK and JWT token in body
     * @throws RuntimeException if username is already taken (propagates from service)
     */
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequest signupRequest) {
        JwtResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    /*
     * Authenticates a user and returns a JWT token.
     * 
     * @param loginRequest the login request containing username and password
     * @return ResponseEntity with HTTP 200 OK and JWT token in body
     * @throws BadCredentialsException if username or password is invalid (returns 401)
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /*
     * Simple health check endpoint for testing controller availability.
     * 
     * @return a simple string message confirming the controller is working
     */
    @GetMapping("/test")
    public String test() {
        return "AuthController is working.";
    }
}
