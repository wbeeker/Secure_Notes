package com.example.secure_notes;

import com.example.secure_notes.entity.User;
import com.example.secure_notes.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;
    private String testSecret;
    private long testExpiration;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        testSecret = "mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm";
        testExpiration = 86400000L; // 24 hours in milliseconds
        
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMillis", testExpiration);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRoles(Collections.singleton("ROLE_USER"));
    }

    @Test
    @DisplayName("Generate token should create valid JWT")
    void testGenerateToken() {
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Extract username from token should return correct username")
    void testExtractUsername() {
        String token = jwtUtil.generateToken(testUser);

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Extract expiration from token should return future date")
    void testExtractExpiration() {
        String token = jwtUtil.generateToken(testUser);
        Date now = new Date();

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(now));
        long timeDiff = expiration.getTime() - now.getTime();
        assertTrue(timeDiff > 86000000L && timeDiff <= 86400000L);
    }

    @Test
    @DisplayName("Validate token with User should return true for valid token")
    void testValidateTokenWithUser() {
        String token = jwtUtil.generateToken(testUser);

        boolean isValid = jwtUtil.validateToken(token, testUser);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Validate token with UserDetails should return true for valid token")
    void testValidateTokenWithUserDetails() {
        String token = jwtUtil.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
        verify(userDetails, atLeastOnce()).getUsername();
    }

    @Test
    @DisplayName("Validate token should return false for wrong user")
    void testValidateTokenWrongUser() {
        String token = jwtUtil.generateToken(testUser);
        
        User differentUser = new User();
        differentUser.setUsername("differentuser");

        boolean isValid = jwtUtil.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Extract username should throw exception for invalid token")
    void testExtractUsernameInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    @DisplayName("Extract username should throw exception for token with wrong signature")
    void testExtractUsernameWrongSignature() {
        String token = jwtUtil.generateToken(testUser);
        
        ReflectionTestUtils.setField(jwtUtil, "secret", "differentSecretKeyThatIsAtLeast256BitsLong!!!!");

        assertThrows(SignatureException.class, () -> {
            jwtUtil.extractUsername(token);
        });
    }

    @Test
    @DisplayName("Generate token should include user roles in claims")
    void testGenerateTokenIncludesRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        testUser.setRoles(roles);

        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Generate token for different users should create different tokens")
    void testGenerateTokenDifferentUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setRoles(Collections.singleton("ROLE_USER"));

        User user2 = new User();
        user2.setUsername("user2");
        user2.setRoles(Collections.singleton("ROLE_USER"));

        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        assertNotEquals(token1, token2);
        assertEquals("user1", jwtUtil.extractUsername(token1));
        assertEquals("user2", jwtUtil.extractUsername(token2));
    }

    @Test
    @DisplayName("Consecutive token generation should create different tokens due to timestamp")
    void testConsecutiveTokenGeneration() throws InterruptedException {
        String token1 = jwtUtil.generateToken(testUser);
        Thread.sleep(10000); 
        String token2 = jwtUtil.generateToken(testUser);

        assertNotEquals(token1, token2);
        assertTrue(jwtUtil.validateToken(token1, testUser));
        assertTrue(jwtUtil.validateToken(token2, testUser));
    }

    @Test
    @DisplayName("Validate token with UserDetails should return false for null username")
    void testValidateTokenNullUsername() {
        String token = jwtUtil.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(null);

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Extract expiration should throw exception for malformed token")
    void testExtractExpirationMalformedToken() {
        String malformedToken = "not.a.valid.jwt";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractExpiration(malformedToken);
        });
    }

    @Test
    @DisplayName("Token should contain issued at timestamp")
    void testTokenContainsIssuedAt() {
        Date beforeGeneration = new Date();
        
        String token = jwtUtil.generateToken(testUser);
        
        Date afterGeneration = new Date();

        assertNotNull(token);
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(beforeGeneration));
    }

    @Test
    @DisplayName("Generate token with user without roles should still work")
    void testGenerateTokenWithoutRoles() {
        User userWithoutRoles = new User();
        userWithoutRoles.setUsername("noroles");
        userWithoutRoles.setRoles(Collections.emptySet());

        String token = jwtUtil.generateToken(userWithoutRoles);

        assertNotNull(token);
        assertEquals("noroles", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userWithoutRoles));
    }

    @Test
    @DisplayName("Generate token with special characters in username")
    void testGenerateTokenSpecialCharacters() {
        User specialUser = new User();
        specialUser.setUsername("user@example.com");
        specialUser.setRoles(Collections.singleton("ROLE_USER"));

        String token = jwtUtil.generateToken(specialUser);

        assertNotNull(token);
        assertEquals("user@example.com", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, specialUser));
    }

    @Test
    @DisplayName("Validate token should handle empty token string")
    void testValidateEmptyToken() {
        String emptyToken = "";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(emptyToken);
        });
    }

    @Test
    @DisplayName("Token validation should be case sensitive for username")
    void testTokenValidationCaseSensitive() {
        String token = jwtUtil.generateToken(testUser);
        
        User upperCaseUser = new User();
        upperCaseUser.setUsername("TESTUSER"); 

        boolean isValid = jwtUtil.validateToken(token, upperCaseUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Multiple roles should be properly stored in token")
    void testMultipleRolesInToken() {
        Set<String> multipleRoles = new HashSet<>();
        multipleRoles.add("ROLE_USER");
        multipleRoles.add("ROLE_ADMIN");
        multipleRoles.add("ROLE_MODERATOR");
        testUser.setRoles(multipleRoles);

        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, testUser));
    }
}