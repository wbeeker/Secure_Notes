package com.example.secure_notes.security;

import com.example.secure_notes.entity.User;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.security.Key;

/*
 * Utility class for JWT operations. 
 */
@Component
public class JwtUtil {
    /*
     * The secret key used for signing JWT tokens. Minimum 256 bits for HS256.
     */
    @Value("${jwt.secret}")
    private String secret;

    /*
     * The token expiration time in milliseconds.
     */
    @Value("${jwt.expiration}")
    private long expirationMillis;

    /*
     * Generates a cryptographic signing key from the secret. 
     * 
     * Converts the string secret into a proper HMAC-SHA256 key for signing 
     * and verifying JWT tokens. 
     * 
     * @return a Key suitable for HMAC-SHA256 signing
     * @throws WeakKeyException if secret is too short
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /*
     * Generates a JWT for a user.
     * 
     * @param user the user for whom to generate the token
     * @return a signed JWT token string
     * @throws IllegalArgumentException if user or user's username is null
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        return createToken(claims, user.getUsername());
    }

    /*
     * Creates a JWT token with the specified claims and subject.
     * 
     * This is the internal implementation that builds the actual JWT token with 
     * all required fields: claims, subject, timestamps, and signature. 
     * 
     * Token Structure Created:
     *  {
     *      "roles": [...],
     *      "sub": "username",
     *      "iat":1682505600,
     *      "exp":1682592000
     *  }
     * 
     * @param claims additional claims to include in the token (e.g. roles)
     * @param subject the subject of the token (typically username)
     * @return a compan, URL-safe JWT string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    /*
     * Extracts the username from a JWT token. Parses the token and retrieves
     * the subject claim, which contains the username. 
     * 
     * @param token the JWT token string (must be valid and signed)
     * @return the username extracted from the token's subject claim
     * @throws ExpiredJwtException if token has expired
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature is invalid
     * @throws UnsupportedJwtException if token format is unsupported
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
     * Extracts the expiration date from a JWT 
     * 
     * @param token the JWT token string
     * @return the expiration Date from the token
     * @throws ExpiredJwtException if token has expired
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature is invalid
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /*
     * Validates a JWT token against a user. Checks that
     * signature is valid, not expired, username matches. 
     * 
     * @param token the JWT token to validate
     * @param user the user to validate against
     * @return true if token is valid for the user, false otherwise
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature is invalid
     */
    public boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    /*
     * Validates a JWT token against Spring Security UserDetails.
     * 
     * @param token the JWT token to validate
     * @param userDetails the Spring Security UserDetails to validate against
     * @return true if token is valid for the user, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /*
     * Checks if a JWT token has expired. 
     * 
     * @param token the JWT token to check
     * @return true if token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());

    }

    /*
     * Extracts all claims from a JWT token.
     * 
     * @param token the JWT token to parse
     * @return the Claims object containing all token claims
     * @throws ExpiredJwtException if token has expired
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature verification fails
     * @throws UnsupportedJwtException if token format is unsupported
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
}
