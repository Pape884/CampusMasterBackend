package com.example.campusMaster.application.dto.response.users;


import com.example.campusMaster.domain.enums.Role;

public record UserResponse(
    Long id,
    String matricule,
    String prenom,
    String nom,
    String email,
    Long telephone,
    Role role,
    Boolean isActive
) {}
