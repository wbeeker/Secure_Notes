package com.example.secure_notes.service;

import com.example.secure_notes.entity.Note;
import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.NoteRepository;
import com.example.secure_notes.repository.UserRepository;
import com.example.secure_notes.util.AesEncryptionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final AesEncryptionUtil aesEncryptionUtil;

    @Autowired
    public NoteService(NoteRepository noteRepository, AesEncryptionUtil aesEncryptionUtil) {
        this.noteRepository = noteRepository;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }
    
    // CREATE
    public Note createNote(String title, String content, User user) {
        String encrypted = aesEncryptionUtil.encrypt(content);
        Note note = new Note();
        note.setTitle(title != null ? title : "Untitled Note");
        note.setContent(encrypted);
        note.setUser(user);
        return noteRepository.save(note);
    }

    // Read ALL for user
    public List<Note> getAllNotesForUser(User user) {
        return noteRepository.findByUser(user).stream().map(note -> {
            note.setContent(aesEncryptionUtil.decrypt(note.getContent()));
            return note;
        }).collect(Collectors.toList());
    }

    // READ SINGLE
    public Optional<Note> getNoteById(Long id, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            note.setContent(aesEncryptionUtil.decrypt(note.getContent()));
            return note;
        });
    }

    // UPDATE
    public Optional<Note> updateNote(Long id, String newContent, User user) {
        return noteRepository.findByIdAndUser(id, user).map(note -> {
            note.setContent(aesEncryptionUtil.encrypt(newContent));
            return noteRepository.save(note);
        });
    }

    // DELETE
    public boolean deleteNote(Long id, User user) {
        Optional<Note> optionalNote = noteRepository.findByIdAndUser(id, user);
        if (optionalNote.isPresent()) {
            noteRepository.delete(optionalNote.get());
            return true;
        }
        return false;
    }
}
