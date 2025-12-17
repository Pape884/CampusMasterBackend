package com.example.campusMaster.application.dto.request;
import com.example.campusMaster.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResisterRequest( @NotBlank(message = "Prénom obligatoire")
    String prenom,
    
    @NotBlank(message = "Nom obligatoire")
    String nom,
    
    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    String email,
    
    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8, message = "Minimum 8 caractères")
    String password,
    
    Role role){}
