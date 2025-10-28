package com.example.secure_notes.security;

import com.example.secure_notes.entity.User;
import com.example.secure_notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/*
 * Custom implementation of Spring Security's UserDetailsService.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /*
     * Repository for accessing user data from the database.
     */
    private final UserRepository userRepository;

    /*
     * Constructs a new CustomUserDetailsService with the required repository.
     * 
     * @param userRepository the repository for user data access
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Loads user-specific data by username for Spring Security authentication. 
     * 
     * @param username the username identifying the user whose data is required
     * @return a fully populated UserDetails object containing user information
     *         and authorities (never null)
     * @throws UsernameNotFoundException if the user could not be found in the database
     *         or the username is null
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPasswordHash(),
            mapRolesToAuthorities(user.getRoles())
        );
    }

    /*
     * Converts role strings to Spring Security's SimpleGrantedAuthority objects.
     * 
     * @param roles the set of role strings from the User entity (e.g., ["ROLE_USER", "ROLE_ADMIN"])
     * @return a set of SimpleGrantedAuthority objects that Spring Security
     *         can use for authorization decisions
     * @throws NullPointerException if roles parameter is null
     */
    private Set<SimpleGrantedAuthority> mapRolesToAuthorities(Set<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
    
}
