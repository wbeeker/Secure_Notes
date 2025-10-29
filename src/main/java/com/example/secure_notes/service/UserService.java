package com.example.secure_notes.service;

import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
 * Service class for user management operations. Provides abstraction layer
 * over UserRepository, offering basic user data access operations. 
 */
@Service
public class UserService {
    /*
     * Repository for user data persistence and retrieval.
     */
    private final UserRepository userRepository;

    /*
     * Constructs a UserService with the required repository. 
     * 
     * @param userRepository the repository for user data operations
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Finds a user by their username.
     * 
     * @param username the username to search for (should not be null)
     * @return an Optional containing the user if found, or Optional.empty() if not found
     * @throws DataAccessException if database query fails
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /*
     * Saves a user entity to the database.
     * 
     * @param user the user entity to save (must not be null)
     * @return the saved user entity with generated ID if new, or updated state if existing
     * @throws IllegalArgumentException if user is null
     * @throws DataIntegrityViolationException if constraints violated
     * @throws DataAccessException if database operation fails
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
