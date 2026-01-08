package com.example.campusMaster.domain.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @Operation(summary = "Mettre à jour le profil")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestParam String prenom,
            @RequestParam String nom
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        UserResponse updated = userService.updateProfile(currentUser.id(), prenom, nom);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profil mis à jour"));
    }
    
    @Operation(summary = "Changer le mot de passe")
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        userService.changePassword(currentUser.id(), oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Mot de passe modifié"));
    }
    
    @Operation(summary = "Lister tous les utilisateurs (ADMIN)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @Operation(summary = "Récupérer un utilisateur par ID (ADMIN)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @Operation(summary = "Lister les utilisateurs par rôle (ADMIN)")
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable Role role
    ) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @Operation(summary = "Désactiver un utilisateur (ADMIN)")
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Utilisateur désactivé"));
    }
    
    @Operation(summary = "Activer un utilisateur (ADMIN)")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Utilisateur activé"));
    }
    
    @Operation(summary = "Statistiques des utilisateurs par rôle (ADMIN)")
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@RequestParam Role role) {
        Long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
