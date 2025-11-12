package com.example.secure_notes.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

/*
 * Spring Security configuration class. 
 * 
 * Configures the security infrastructure for the application, including
 * authentication, authorization, JWT token validation, CORS policies, and session
 * management. Implements a stateless, token-based security model using JWT for API
 * authentication.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /*
     * Configures the security filter chain for HTTP requests. 
     * 
     * Note: CSRF is disabled because this is a stateless API using JWT tokens. 
     * 
     * @param http the HttpSecurity to configure
     * @param jwtAuthenticationFilter the custom JWT filter to validate tokens
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
        .cors(cors -> cors.configurationSource(corsConfigurationSourch()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/health").permitAll()
        .requestMatchers("/", "/index.html").permitAll()
        .requestMatchers("/swagger-ui/**").permitAll()
        .requestMatchers("/v3/api-docs/**").permitAll()
        .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    /*
     * The AuthenticationManager is responsible for authenticating users by
     * validating their credentials (username and password). It is used by
     * the AuthService during login to verify user credentials before
     * generation JWT tokens. 
     * 
     * Authentication Process:
     *  User submits usernam and password via login endpoint
     *  AuthService calls authenticationManager.authenticate()
     *  AuthenticationManager uses UserDetailsService to load user
     *  PasswordEncoder compares submitted password with stored hash
     *  If valid, authentication succeeds and JWT token is generated
     *  If valid, BadCredentialsException is thrown
     * 
     * @param config the authentication configuration provided by Spring Security
     * @return the configured AuthenticationManager
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * Provides the password encoder for hasing and verifying passwords.
     * 
     * Uses BCrypt hashing algorithm with a default strength factor of 10 rounds. 
     * 
     * @return a BCryptPasswordEncoder instance for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Configures CORS settings for the application. Controls whic domains can make
     * requests to the API. Allows frontend and backend to communicate. 
     * 
     * Note: This setup is for development, but not secure for production. 
     * 
     * @return the configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSourch() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 
