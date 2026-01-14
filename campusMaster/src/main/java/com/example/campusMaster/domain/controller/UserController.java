package com.example.campusMaster.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.request.CreateUserRequest;
import com.example.campusMaster.application.dto.request.UpdateUserRequest;
import com.example.campusMaster.application.dto.request.UpdateStatusRequest;
import com.example.campusMaster.application.dto.response.PageResponse;
import com.example.campusMaster.application.dto.response.Pagination;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.application.dto.response.UserStats;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "Lister tous les utilisateurs avec pagination et filtres")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Page<User> users = userService.getUsers(search, role, isActive, page, limit, sortBy, sortOrder);
        UserStats stats = userService.getUserStats();
        
        PageResponse<UserResponse> response = PageResponse.<UserResponse>builder()
                .data(users.getContent().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()))
                .pagination(Pagination.builder()
                        .page(page)
                        .limit(limit)
                        .total(users.getTotalElements())
                        .totalPages(users.getTotalPages())
                        .build())
                .stats(stats)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Mettre à jour le profil de l'utilisateur connecté")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestParam String prenom,
            @RequestParam String nom) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        UserResponse updated = userService.updateProfile(currentUser.id(), prenom, nom);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "Changer le mot de passe de l'utilisateur connecté")
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        userService.changePassword(currentUser.id(), oldPassword, newPassword);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Récupérer un utilisateur par ID (ADMIN)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Créer un nouvel utilisateur (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @Operation(summary = "Mettre à jour un utilisateur (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Modifier le statut d'un utilisateur (ADMIN)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        UserResponse user = userService.updateStatus(id, request.getIsActive());
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Activer un utilisateur (ADMIN)")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Désactiver un utilisateur (ADMIN)")
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Supprimer un utilisateur (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Statistiques des utilisateurs (ADMIN)")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStats> getUserStats() {
        UserStats stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }
    
    @Operation(summary = "Rechercher des utilisateurs (ADMIN)")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<UserResponse> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
    
    private UserResponse convertToResponse(User user) {
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