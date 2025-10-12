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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Skip authentication for auth endpoints
        String requestPath = request.getRequestURI();
        System.out.println("== JWT Filter Processing: " + requestPath); // debug

        if (requestPath.startsWith("/api/auth/") || requestPath.startsWith("/api/health")) {
            System.out.println("Skipping JWT for: " + requestPath); // debug
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("Auth header present: " + (authHeader != null)); // debug

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token found"); // debug
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        System.out.println("Token starts with: " + token.substring(0, 10)); // debug

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
