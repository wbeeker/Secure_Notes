package com.example.secure_notes.repository;

import com.example.secure_notes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repository interface for User entity database operations.
 * 
 * This interface extends JpaRepository to provide CRUD operations and custom
 * query methods for managing notes in the database. Spring Data JPA automatically implements 
 * this interface at runtime. 
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * Retrieves a user by their username.
     * 
     * This is the primary method for user authentication and authorization. 
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found
     * @throws IllegalArgumentException if username is null
     */
    Optional<User> findByUsername(String username);
}
