package com.example.secure_notes;

import com.example.secure_notes.entity.Note;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.service.NoteService;

import com.example.secure_notes.repository.NoteRepository;
import com.example.secure_notes.util.AesEncryptionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;

    @Mock
    private AesEncryptionUtil aesEncryptionUtil;

    @InjectMocks
    private NoteService noteService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void testCreateNote() {
        String title = "My Note";
        String content = "Secret content";
        String encryptedContent = "EncryptedSecret";

        when(aesEncryptionUtil.encrypt(content)).thenReturn(encryptedContent);

        Note savedNote = new Note();
        savedNote.setId(1L);
        savedNote.setTitle(title);
        savedNote.setContent(encryptedContent);
        savedNote.setUser(user);

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        Note result = noteService.createNote(title, content, user);

        assertNotNull(result);
        assertEquals(savedNote.getId(), result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(encryptedContent, result.getContent());
        assertEquals(user, result.getUser());

        verify(aesEncryptionUtil).encrypt(content);
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void testGetAllNotesForUser() {
        Note note1 = new Note();
        note1.setContent("Encrypted1");
        Note note2 = new Note();
        note2.setContent("Encrypted2");

        when(noteRepository.findByUser(user)).thenReturn(Arrays.asList(note1, note2));
        when(aesEncryptionUtil.decrypt("Encrypted1")).thenReturn("Decrypted1");
        when(aesEncryptionUtil.decrypt("Encrypted2")).thenReturn("Decrypted2");

        List<Note> notes = noteService.getAllNotesForUser(user);

        assertEquals(2, notes.size());
        assertEquals("Decrypted1", notes.get(0).getContent());
        assertEquals("Decrypted2", notes.get(1).getContent());

        verify(aesEncryptionUtil).decrypt("Encrypted1");
        verify(aesEncryptionUtil).decrypt("Encrypted2");
    }

    @Test
    void testGetNoteById() {
        Note note = new Note();
        note.setId(1L);
        note.setContent("EncryptedContent");

        when(noteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(note));
        when(aesEncryptionUtil.decrypt("EncryptedContent")).thenReturn("DecryptedContent");

        Optional<Note> result = noteService.getNoteById(1L, user);

        assertTrue(result.isPresent());
        assertEquals("DecryptedContent", result.get().getContent());
        verify(aesEncryptionUtil).decrypt("EncryptedContent");
    }

    @Test
    void testUpdateNote() {
        Note existingNote = new Note();
        existingNote.setId(1L);
        existingNote.setContent("OldEncrypted");
        existingNote.setTitle("Old Title");

        when(noteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingNote));
        when(aesEncryptionUtil.encrypt("New content")).thenReturn("NewEncrypted");

        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("New Title");
        updatedNote.setContent("NewEncrypted");
        updatedNote.setUser(user);

        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        Optional<Note> result = noteService.updateNote(1L, "New Title", "New content", user);

        assertTrue(result.isPresent());
        assertEquals("New Title", result.get().getTitle());
        assertEquals("NewEncrypted", result.get().getContent());
        verify(aesEncryptionUtil).encrypt("New content");
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void testDeleteNoteSuccess() {
        Note note = new Note();
        note.setId(1L);

        when(noteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(note));

        boolean deleted = noteService.deleteNote(1L, user);

        assertTrue(deleted);
        verify(noteRepository).delete(note);
    }

    @Test
    void testDeleteNoteFailure() {
        when(noteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        boolean deleted = noteService.deleteNote(1L, user);

        assertFalse(deleted);
        verify(noteRepository, never()).delete(any());
    }
}
