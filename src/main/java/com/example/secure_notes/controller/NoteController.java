package com.example.secure_notes.controller;

import com.example.secure_notes.dto.CreateNoteRequest;
import com.example.secure_notes.entity.Note;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import com.example.secure_notes.service.NoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/*
 * REST controller for managing encrypted notes.
 * 
 * Provides a complete CRUD API for secure notes. 
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    /*
     * Service layer dependency for note business logic.
     */
    private final NoteService noteService;

    /*
     * Repository for user data access.
     */
    private final UserRepository userRepository;

    /*
     * Constructs a NoteController with required dependencies.
     * 
     * @param noteService service for note operations
     * @param userRepository repository for user lookup
     */
    @Autowired
    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }
    
    /*
     * Creates a new encrypted note for the authenticated user.
     * 
     * @param request the note creation request containing title and content
     * @param userDetails the authenticated user's details (injected by Spring Security)
     * @return ResponseEntity with HTTP 200 OK and created note in body
     * @throws RuntimeException if authenticated user not found in database
     */
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody CreateNoteRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Note createdNote = noteService.createNote(request.getTitle(), request.getContent(), user);
        return ResponseEntity.ok(createdNote);
    }

    /*
     * Retrieves all notes for the authenticated user with decrypted content.
     * 
     * @param userDetails the authenticated user's details
     * @return ResponseEntity with HTTP 200 OK and list of decrypted notes
     * @throws RuntimeException if authenticated user not found in database
     */
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        List<Note> notes = noteService.getAllNotesForUser(user);
        return ResponseEntity.ok(notes);
    }
    
    /*
     * Retrieves a specific note by ID with decrypted content.
     * 
     * @param id the ID of the note to retrieve (from path variable)
     * @param userDetails the authenticated user's details
     * @return ResponseEntity with HTTP 200 OK and note, or 404 Not Found
     * @throws RuntimeException if authenticated user not found in database
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Optional<Note> note = noteService.getNoteById(id, user);
        return note.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /*
     * Updates an existing note's title and content with re-encryption.
     * 
     * @param id the ID of the note to update
     * @param request the update request containing new title and content
     * @param userDetails the authenticated user's details
     * @return ResponseEntity with HTTP 200 OK and updated note, or 404 Not Found
     * @throws RuntimeException if authenticated user not found in database
     */
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody CreateNoteRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Optional<Note> updatedNote = noteService.updateNote(id, request.getTitle(), request.getContent(), user);
        return updatedNote.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /*
     * Deletes a note by ID.
     * 
     * @param id the ID of the note to delete
     * @param userDetails the authenticated user's details
     * @return ResponseEntity with HTTP 204 No Content if deleted, or 404 Not Found
     * @throws RuntimeException if authenticated user not found in database
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        boolean deleted = noteService.deleteNote(id, user);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
