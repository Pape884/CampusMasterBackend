package com.example.campusMaster.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class SimpleUserDetailsService implements UserDetailsService {
     @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Utilisateurs en mémoire pour DÉVELOPPEMENT
        if ("admin@campus.com".equals(username)) {
            return User.builder()
                    .username("admin@campus.com")
                    .password("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBoDGBZbB.6LpK") // "admin123"
                    .roles("ADMIN")
                    .build();
        }
        
        if ("teacher@campus.com".equals(username)) {
            return User.builder()
                    .username("teacher@campus.com")
                    .password("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBoDGBZbB.6LpK") // "admin123"
                    .roles("TEACHER")
                    .build();
        }
        
        if ("student@campus.com".equals(username)) {
            return User.builder()
            .username("student@campus.com")
                    .password("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBoDGBZbB.6LpK") // "admin123"
                    .roles("STUDENT")
                    .build();
        }
        
        throw new UsernameNotFoundException("User not found: " + username);
    }

}
