package com.example.campusMaster.application.dto.response.users;


import java.util.List;

import com.example.campusMaster.application.dto.response.enrollement.EnrollmentResponse;
import com.example.campusMaster.domain.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String matricule;
    private String prenom;
    private String nom;
    private String email;
    private Long telephone;
    private Role role;
    private Boolean isActive;
    private List<EnrollmentResponse> enrollments;
}
    