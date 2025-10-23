package com.example.secure_notes;

import com.example.secure_notes.entity.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

class UserTest {

    private User user;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        user = new User();
        testTime = LocalDateTime.now();
    }

    @Test
    @DisplayName("Test default constructor initializes empty collections")
    void testDefaultConstructor() {
        assertNotNull(user.getRoles());
        assertNotNull(user.getNotes());
        assertTrue(user.getRoles().isEmpty());
        assertTrue(user.getNotes().isEmpty());
    }

    @Test
    @DisplayName("Test parameterized constructor sets all fields")
    void testParameterizedConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");
        
        List<Note> notes = new ArrayList<>();
        
        User user = new User(1L, "testuser", "test@example.com", 
                           "hashedPassword", roles, notes, testTime);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains("USER"));
        assertTrue(user.getRoles().contains("ADMIN"));
        assertEquals(notes, user.getNotes());
        assertEquals(testTime, user.getTimeCreated());
    }

    @Test
    @DisplayName("Test setId and getId")
    void testIdGetterSetter() {
        user.setId(100L);
        assertEquals(100L, user.getId());
    }

    @Test
    @DisplayName("Test setUsername and getUsername")
    void testUsernameGetterSetter() {
        user.setUsername("johndoe");
        assertEquals("johndoe", user.getUsername());
    }

    @Test
    @DisplayName("Test setEmail and getEmail")
    void testEmailGetterSetter() {
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Test setPasswordHash and getPasswordHash")
    void testPasswordHashGetterSetter() {
        user.setPasswordHash("$2a$10$encrypted");
        assertEquals("$2a$10$encrypted", user.getPasswordHash());
    }

    @Test
    @DisplayName("Test setRoles and getRoles")
    void testRolesGetterSetter() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("MODERATOR");
        
        user.setRoles(roles);
        
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains("USER"));
        assertTrue(user.getRoles().contains("MODERATOR"));
    }

    @Test
    @DisplayName("Test adding roles to existing set")
    void testAddingRoles() {
        user.getRoles().add("USER");
        user.getRoles().add("ADMIN");
        
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains("USER"));
        assertTrue(user.getRoles().contains("ADMIN"));
    }

    @Test
    @DisplayName("Test setNotes and getNotes")
    void testNotesGetterSetter() {
        List<Note> notes = new ArrayList<>();
        
        user.setNotes(notes);
        
        assertEquals(notes, user.getNotes());
        assertTrue(user.getNotes().isEmpty());
    }

    @Test
    @DisplayName("Test setTimeCreated and getTimeCreated")
    void testTimeCreatedGetterSetter() {
        user.setTimeCreated(testTime);
        assertEquals(testTime, user.getTimeCreated());
    }

    @Test
    @DisplayName("Test user with null email")
    void testNullEmail() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("Test roles set uniqueness")
    void testRolesUniqueness() {
        user.getRoles().add("USER");
        user.getRoles().add("USER");
        user.getRoles().add("ADMIN");
        
        // Set should only contain 2 unique values
        assertEquals(2, user.getRoles().size());
    }
}