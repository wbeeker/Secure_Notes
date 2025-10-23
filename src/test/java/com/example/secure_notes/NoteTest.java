package com.example.secure_notes;

import com.example.secure_notes.entity.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class NoteTest {

    private Note note;
    private User testUser;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        note = new Note();
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testTime = LocalDateTime.now();
    }

    @Test
    @DisplayName("Test default constructor creates empty note")
    void testDefaultConstructor() {
        assertNotNull(note);
        assertNull(note.getId());
        assertNull(note.getTitle());
        assertNull(note.getContent());
        assertNull(note.getUser());
        assertNull(note.getTimeCreated());
        assertNull(note.getTimeUpdated());
    }

    @Test
    @DisplayName("Test parameterized constructor sets all fields")
    void testParameterizedConstructor() {
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();
        
        Note note = new Note(1L, "My Note", "Encrypted content here", 
                           testUser, created, updated);

        assertEquals(1L, note.getId());
        assertEquals("My Note", note.getTitle());
        assertEquals("Encrypted content here", note.getContent());
        assertEquals(testUser, note.getUser());
        assertEquals(created, note.getTimeCreated());
        assertEquals(updated, note.getTimeUpdated());
    }

    @Test
    @DisplayName("Test setId and getId")
    void testIdGetterSetter() {
        note.setId(100L);
        assertEquals(100L, note.getId());
    }

    @Test
    @DisplayName("Test setTitle and getTitle")
    void testTitleGetterSetter() {
        note.setTitle("Shopping List");
        assertEquals("Shopping List", note.getTitle());
    }

    @Test
    @DisplayName("Test setContent and getContent")
    void testContentGetterSetter() {
        String encryptedContent = "abc123encrypted456xyz";
        note.setContent(encryptedContent);
        assertEquals(encryptedContent, note.getContent());
    }

    @Test
    @DisplayName("Test setUser and getUser")
    void testUserGetterSetter() {
        note.setUser(testUser);
        assertEquals(testUser, note.getUser());
        assertEquals(1L, note.getUser().getId());
        assertEquals("testuser", note.getUser().getUsername());
    }

    @Test
    @DisplayName("Test setTimeCreated and getTimeCreated")
    void testTimeCreatedGetterSetter() {
        note.setTimeCreated(testTime);
        assertEquals(testTime, note.getTimeCreated());
    }

    @Test
    @DisplayName("Test setTimeUpdated and getTimeUpdated")
    void testTimeUpdatedGetterSetter() {
        note.setTimeUpdated(testTime);
        assertEquals(testTime, note.getTimeUpdated());
    }

    @Test
    @DisplayName("Test note with long content")
    void testLongContent() {
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("This is a very long encrypted note content. ");
        }
        
        note.setContent(longContent.toString());
        
        assertEquals(longContent.toString(), note.getContent());
        assertTrue(note.getContent().length() > 10000);
    }

    @Test
    @DisplayName("Test note with empty title")
    void testEmptyTitle() {
        note.setTitle("");
        assertEquals("", note.getTitle());
    }

    @Test
    @DisplayName("Test note with empty content")
    void testEmptyContent() {
        note.setContent("");
        assertEquals("", note.getContent());
    }

    @Test
    @DisplayName("Test updating note content")
    void testUpdatingContent() {
        note.setContent("Original content");
        assertEquals("Original content", note.getContent());
        
        note.setContent("Updated content");
        assertEquals("Updated content", note.getContent());
    }

    @Test
    @DisplayName("Test note timestamps for creation and update")
    void testTimestamps() {
        LocalDateTime created = LocalDateTime.now().minusHours(2);
        LocalDateTime updated = LocalDateTime.now();
        
        note.setTimeCreated(created);
        note.setTimeUpdated(updated);
        
        assertTrue(note.getTimeCreated().isBefore(note.getTimeUpdated()));
    }

    @Test
    @DisplayName("Test changing note owner")
    void testChangingOwner() {
        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setUsername("user1");
        
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("user2");
        
        note.setUser(firstUser);
        assertEquals(1L, note.getUser().getId());
        
        note.setUser(secondUser);
        assertEquals(2L, note.getUser().getId());
        assertEquals("user2", note.getUser().getUsername());
    }

    @Test
    @DisplayName("Test complete note lifecycle")
    void testCompleteNoteLifecycle() {
        LocalDateTime now = LocalDateTime.now();
        
        note.setId(1L);
        note.setTitle("Meeting Notes");
        note.setContent("Encrypted meeting content");
        note.setUser(testUser);
        note.setTimeCreated(now);
        note.setTimeUpdated(now);

        assertAll("Note properties",
            () -> assertEquals(1L, note.getId()),
            () -> assertEquals("Meeting Notes", note.getTitle()),
            () -> assertEquals("Encrypted meeting content", note.getContent()),
            () -> assertNotNull(note.getUser()),
            () -> assertEquals(testUser, note.getUser()),
            () -> assertEquals(now, note.getTimeCreated()),
            () -> assertEquals(now, note.getTimeUpdated())
        );
    }

    @Test
    @DisplayName("Test note with special characters in title")
    void testSpecialCharactersInTitle() {
        String specialTitle = "Note: @#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";
        note.setTitle(specialTitle);
        assertEquals(specialTitle, note.getTitle());
    }

    @Test
    @DisplayName("Test note with unicode characters")
    void testUnicodeCharacters() {
        note.setTitle("Notes 📝 Important ⭐");
        note.setContent("Content with emoji 🔒 and unicode 你好");
        
        assertEquals("Notes 📝 Important ⭐", note.getTitle());
        assertTrue(note.getContent().contains("🔒"));
        assertTrue(note.getContent().contains("你好"));
    }

    @Test
    @DisplayName("Test note association with user bidirectionally")
    void testBidirectionalUserAssociation() {
        note.setId(1L);
        note.setTitle("Test Note");
        note.setUser(testUser);
        
        testUser.getNotes().add(note);
        
        assertTrue(testUser.getNotes().contains(note));
        assertEquals(testUser, note.getUser());
    }

    @Test
    @DisplayName("Test null user assignment")
    void testNullUser() {
        note.setUser(testUser);
        assertEquals(testUser, note.getUser());
        
        note.setUser(null);
        assertNull(note.getUser());
    }
}