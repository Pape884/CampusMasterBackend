package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.Services.AuthService;
import com.example.campusMaster.application.dto.request.LoginRequest;
import com.example.campusMaster.application.dto.response.AuthResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping ("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints d'authentification")
public class AuthController {
    private final AuthService authService;
 
    @Operation(summary = "Connexion utilisateur")
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        ApiSuccessResponse<AuthResponse> apiResponse = ApiSuccessResponse.<AuthResponse>builder()
                .success(true)
                .message("Connexion réussie")
                .data(response)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Renouveler le token JWT")
    @PostMapping("/refresh")
    public ResponseEntity<ApiSuccessResponse<AuthResponse>> refreshToken(
            @RequestParam String refreshToken
    ) {
        AuthResponse response = authService.refreshToken(refreshToken);
        ApiSuccessResponse<AuthResponse> apiResponse = ApiSuccessResponse.<AuthResponse>builder()
                .success(true)
                .message("Token renouvelé")
                .data(response)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Demander une réinitialisation de mot de passe")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiSuccessResponse<String>> forgotPassword(
            @RequestParam String email
    ) {
        authService.resetPassword(email);
        return ResponseEntity.ok(
            ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Email de réinitialisation envoyé")
                .data(null)
                .timestamp(java.time.LocalDateTime.now())
                .build()
        );
    }

}
