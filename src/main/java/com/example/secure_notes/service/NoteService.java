package com.example.secure_notes.service;

import com.example.secure_notes.entity.Note;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.NoteRepository;
import com.example.secure_notes.util.AesEncryptionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Service class for managing encrypted note operations.
 * 
 * Provides CRUD interface for secure notes, automatically handling 
 * AES-256 encryption and decryption of note content. 
 */
@Service
public class NoteService {

    /*
     * Repository for note data persistence and retrieval.
     */
    private final NoteRepository noteRepository;

    /*
     * Utility for AES-256 encryption and decryption of note content. 
     */
    private final AesEncryptionUtil aesEncryptionUtil;

    /*
     * Constructs a NoteService with required dependencies.
     * 
     * @param noteRepository repository for note database operations
     * @param aesEncryptionUtil utility for content encryption/decryption
     */
    @Autowired
    public NoteService(NoteRepository noteRepository, AesEncryptionUtil aesEncryptionUtil) {
        this.noteRepository = noteRepository;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }
    
    /*
     * Creates a new encrypted note for a user. 
     * 
     * @param title the title of the note (if null, defaults to "Untitled Note")
     * @param content the plaintext content to encrypt and store (must not be null)
     * @param user the user who owns this note (must not be null)
     * @return the created Note entity with encrypted content
     * @throws NullPointerException if content or user is null
     * @throws RuntimeException if encryption or database operation fails
     */
    public Note createNote(String title, String content, User user) {
        String encrypted = aesEncryptionUtil.encrypt(content);
        Note note = new Note();
        note.setTitle(title != null ? title : "Untitled Note");
        note.setContent(encrypted);
        note.setUser(user);
        return noteRepository.save(note);
    }

    /*
     * Retrieves all notes for a user with decrypted content. 
     * 
     * @param id the ID of the note to retrieve (must not be null)
     * @param user the user who should own the note (must not be null)
     * @return an Optional containing the note with decrypted content if found and owned by user,
     *         or Optional.empty() if not found or not owned by user
     * @throws RuntimeException if decryption fails
     * @throws DataAccessException if database query fails
     */
    public List<Note> getAllNotesForUser(User user) {
        return noteRepository.findByUser(user).stream().map(note -> {
            note.setContent(aesEncryptionUtil.decrypt(note.getContent()));
            return note;
        }).collect(Collectors.toList());
    }

    /*
     * Retrieves a single note by ID for a specific user with decrypted content.
     * 
     * @param id the ID of the note to retrieve (must not be null)
     * @param user the user who should own the note (must not be null)
     * @return an Optional containing the note with decrypted content if found and owned by user,
     *         or Optional.empty() if not found or not owned by user
     * @throws RuntimeException if decryption fails
     * @throws DataAccessException if database query fails
     * 
     */
    public Optional<Note> getNoteById(Long id, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            note.setContent(aesEncryptionUtil.decrypt(note.getContent()));
            return note;
        });
    }

    /*
     * Updates an existing note's title and content with authorization check.
     * 
     * @param id the ID of the note to update (must not be null)
     * @param title the new title (if null, defaults to "Untitiled Note" - note the typo)
     * @param content the new plaintext content to encrypt (must not be null)
     * @param user the user who should own the note (must not be null)
     * @return an Optional containing the updated note with encrypted content if found and owned,
     *         or Optional.empty() if not found or not owned by user
     * @throws NullPointerException if content or user is null
     * @throws RuntimeException if encryption or database operation fails
     */
    public Optional<Note> updateNote(Long id, String title, String content, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            note.setTitle(title != null ? title : "Untitiled Note");
            note.setContent(aesEncryptionUtil.encrypt(content));
            return noteRepository.save(note);
        });
    }

    /*
     * Deletes a note with authorization check. 
     * 
     * @param id the ID of the note to delete (must not be null)
     * @param user the user who should own the note (must not be null)
     * @return true if note was found and deleted, false if not found or not owned by user
     * @throws DataAccessException if database operation fails
     */
    public boolean deleteNote(Long id, User user) {
        Optional<Note> optionalNote = noteRepository.findByIdAndUser(id, user);
        if (optionalNote.isPresent()) {
            noteRepository.delete(optionalNote.get());
            return true;
        }
        return false;
    }
}
