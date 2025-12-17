package com.example.campusMaster.application.Services;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return mapToResponse(user);
    }
    
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return mapToResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public UserResponse updateProfile(Long userId, String prenom, String nom) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        user.setPrenom(prenom);
        user.setNom(nom);
        
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }
    
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    public Long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
    
    // Mapper
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getPrenom(),
            user.getNom(),
            user.getEmail(),
            user.getRole(),
            user.getIsActive()
        );
    }

}
