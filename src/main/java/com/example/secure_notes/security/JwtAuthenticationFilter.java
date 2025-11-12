package com.example.secure_notes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * JWT authentication filter that validates JWT tokens on incoming requests.
 * Filters all HTTP requests and validates JWT tokens found in the Authorization header. 
 * If a valid token is present, it extracts the user information
 * and sets up Spring Security authentication context. This enables stateless,
 * token-based authentication throughout the application.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /*
     * Utility class for JWT operations (token parsing, validation, etc.).
     */
    private final JwtUtil jwtUtil;

    /*
     * Service for loading user-specific data.
     */
    private final UserDetailsService userDetailsService;

    /*
     * Constructs a new JwtAuthenticationFilter with required dependencies. 
     * 
     * @param jwtUtil the JWT utility for token operations
     * @param userDetailsService the service for loading user details
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /*
     * Processes each HTTP request to validate JWT tokens and set up authentication.
     * 
     * This method is called once per request by the Spring Security filter chain.
     * 
     * @param request the HTTP servlet request being processed
     * @param response the HTTP servlet response to be sent
     * @param filterChain the filter chain to continue processing after authentication
     * @throws ServletException if a servlet-specific error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering

     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Skip authentication for auth endpoints
        String requestPath = request.getRequestURI();

        if (requestPath.startsWith("/api/auth/") || 
            requestPath.startsWith("/api/health") ||
            requestPath.startsWith("/swagger-ui/") ||
            requestPath.startsWith("/v3/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtUtil.extractUsername(token);
            System.out.println("Extracted username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("Found user: " + userDetails.getUsername());

                if (jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("Token validation SUCCESS");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("Token validation FAILED");
            }
        }
        
        } catch (Exception e) {
            System.out.println("JWT processing error: " + e.getMessage());
        }

        System.out.println("SecurityContext after JWT: " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
    
}
