package com.example.campusMaster.application.dto.response;

public record AuthResponse(
    String token,
    String refreshToken,
    UserResponse user
) {}
