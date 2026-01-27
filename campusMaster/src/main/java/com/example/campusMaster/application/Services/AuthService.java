package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.request.LoginRequest;
import com.example.campusMaster.application.dto.response.AuthResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import com.example.campusMaster.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
   
    /**
     * Connexion d'un utilisateur
     */
    public AuthResponse login(LoginRequest request) {
        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );
        
        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        
        if (!user.getIsActive()) {
            throw new ResourceNotFoundException("Compte désactivé");
        }
        
        // Générer les tokens
        String token = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        
        UserResponse userResponse = mapToUserResponse(user);
        
        return new AuthResponse(token, refreshToken, userResponse);
    }
    
    /**
     * Renouveler le token avec le refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token invalide");
        }
        
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        
        String newToken = jwtTokenProvider.generateToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);
        
        UserResponse userResponse = mapToUserResponse(user);
        
        return new AuthResponse(newToken, newRefreshToken, userResponse);
    }
    
    /**
     * Réinitialiser le mot de passe
     */
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        
        
        // Cette logique sera implémentée avec le EmailService
    }
    
    /**
     * Mapper User vers UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .matricule(user.getMatricule())
            .prenom(user.getPrenom())
            .nom(user.getNom())
            .email(user.getEmail())
            .telephone(user.getTelephone())
            .role(user.getRole())
            .isActive(user.getIsActive())
            .build();
    }
}

