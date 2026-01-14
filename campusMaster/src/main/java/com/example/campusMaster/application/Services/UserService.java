package com.example.campusMaster.application.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campusMaster.application.dto.request.CreateUserRequest;
import com.example.campusMaster.application.dto.request.UpdateUserRequest;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.application.dto.response.UserStats;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Méthode avec pagination et filtres
    public Page<User> getUsers(String search, String role, Boolean isActive, 
                                int page, int limit, String sortBy, String sortOrder) {
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, limit, sort);
        
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            
            // Recherche par nom, prénom ou email
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern)
                );
                predicate = criteriaBuilder.and(predicate, searchPredicate);
            }
            
            // Filtre par rôle
            if (role != null && !role.isEmpty()) {
                try {
                    Role roleEnum = Role.valueOf(role.toUpperCase());
                    predicate = criteriaBuilder.and(predicate, 
                        criteriaBuilder.equal(root.get("role"), roleEnum));
                } catch (IllegalArgumentException e) {
                    // Ignorer si le rôle est invalide
                }
            }
            
            // Filtre par statut actif/inactif
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("isActive"), isActive));
            }
            
            return predicate;
        };
        
        return userRepository.findAll(spec, pageable);
    }
    
    // Statistiques des utilisateurs
    public UserStats getUserStats() {
        long total = userRepository.count();
        long active = userRepository.countByIsActive(true);
        long inactive = userRepository.countByIsActive(false);
        
        long students = userRepository.countByRole(Role.STUDENT);
        long teachers = userRepository.countByRole(Role.TEACHER);
        long admins = userRepository.countByRole(Role.ADMIN);
        
        return UserStats.builder()
                .totalUsers(total)
                .activeUsers(active)
                .inactiveUsers(inactive)
                .studentsCount(students)
                .teachersCount(teachers)
                .adminsCount(admins)
                .build();
    }
    
    // Recherche simple
    public List<UserResponse> searchUsers(String query) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        
        Specification<User> spec = (root, q, criteriaBuilder) -> 
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern)
            );
        
        return userRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    // Créer un utilisateur
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        
        User user = User.builder()
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .email(request.getEmail())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();
        
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }
    
    // Mettre à jour un utilisateur
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }
    
    // Mettre à jour le statut
    public UserResponse updateStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        user.setIsActive(isActive);
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }
    
    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        userRepository.deleteById(id);
    }
    
    // Méthodes existantes
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