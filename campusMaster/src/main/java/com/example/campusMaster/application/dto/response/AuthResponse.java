package com.example.campusMaster.application.dto.response;

import com.example.campusMaster.application.dto.response.users.UserResponse;

public record AuthResponse(
    String token,
    String refreshToken,
    UserResponse user
) {}
