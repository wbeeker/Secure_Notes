package com.example.secure_notes.controller;

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

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    @Autowired
    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }
    
    // CREATE
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Note createdNote = noteService.createNote(content, user);
        return ResponseEntity.ok(createdNote);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        List<Note> notes = noteService.getAllNotesForUser(user);
        return ResponseEntity.ok(notes);
    }

    // READ SINGLE
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Optional<Note> note = noteService.getNoteById(user.getId(), user);
        return note.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody String newContent, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        Optional<Note> updatedNote = noteService.updateNote(id, newContent, user);
        return updatedNote.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found."));
        boolean deleted = noteService.deleteNote(id, user);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }



}
