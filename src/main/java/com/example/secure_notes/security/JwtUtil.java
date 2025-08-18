package com.example.secure_notes.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.security.Key;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    // Helper function to get the signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate a token for a user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap();
        return createToken(claims, userDetails.getUsername());
    }

    // Create the token with claims, subject, timestamp, expiration and signing
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    // Extracts username from JWT
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extracts expiration from token
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Validates the tplem by checking username and expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Checks if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());

    }

    // Extract claims - only used internally
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
}
