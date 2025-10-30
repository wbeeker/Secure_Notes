package com.example.secure_notes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Simple test controller for checking status of /api/ endpoints. 
 */
@RestController
public class HealthController {
    @GetMapping("/api/health")
    public String health() {
        return "Server is running!";
    }
}
