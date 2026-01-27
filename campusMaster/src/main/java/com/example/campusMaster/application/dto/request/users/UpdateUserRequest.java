package com.example.campusMaster.application.dto.request.users;

import com.example.campusMaster.domain.enums.Role;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    private String prenom;

    private String nom;

    @Email(message = "Email invalide")
    private String email;

    private Long telephone;
    
    private Role role;

}
