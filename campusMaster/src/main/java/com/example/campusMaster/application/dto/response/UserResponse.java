package com.example.campusMaster.application.dto.response;


import com.example.campusMaster.domain.enums.Role;

public record UserResponse(
    Long id,
    String prenom,
    String nom,
    String email,
    Role role,
    Boolean isActive
) {}
