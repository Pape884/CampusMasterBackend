package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.Services.AuthService;
import com.example.campusMaster.application.dto.request.LoginRequest;
import com.example.campusMaster.application.dto.request.ResisterRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping ("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints d'authentification")
public class AuthController {
    private final AuthService authService;
    
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody ResisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Inscription réussie"));
    }
    
    @Operation(summary = "Connexion utilisateur")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Connexion réussie"));
    }
    
    @Operation(summary = "Renouveler le token JWT")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam String refreshToken
    ) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response, "Token renouvelé"));
    }
    
    @Operation(summary = "Demander une réinitialisation de mot de passe")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestParam String email
    ) {
        authService.resetPassword(email);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Email de réinitialisation envoyé")
        );
    }

}
