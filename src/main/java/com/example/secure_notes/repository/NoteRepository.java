package com.example.secure_notes.repository;

import com.example.secure_notes.entity.Note;
import com.example.secure_notes.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Repository interface for Note entity database operations.
 * 
 * This interface extends JpaRepository to provide CRUD operations and custom
 * query methods for managing notes in the database. Spring Data JPA automatically implements 
 * this interface at runtime. 
 * 
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    /*
     * Retrieves all notes belonging to a specific user. 
     * 
     * @param user the user whose notes should be retrived
     * @return a list of all notes belonging to the user
     * @throws IllegalArgumentException if user is null
     */
    List<Note> findByUser(User user);

    /*
     * Retrieves a specific note by ID, but only if it belongs to the specified user.
     * 
     * @param id the ID of the note to retrieve
     * @param user the user who should own the note
     * @return an Optional containing the note if ofound and owned by the user
     *  or Optional.empty() if not found or not owned by the user.
     * @throws IllegalArgumentException if id or user is null
     */
    Optional<Note> findByIdAndUser(Long id, User user);
}
