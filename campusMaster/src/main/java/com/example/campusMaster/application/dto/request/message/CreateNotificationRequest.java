package com.example.campusMaster.application.dto.request.message;

import com.example.campusMaster.domain.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(
    @NotNull(message = "User ID obligatoire")
    Long userId,
    
    @NotNull(message = "Type obligatoire")
    NotificationType notificationType,
    
    @NotBlank(message = "Titre obligatoire")
    String title,
    
    String content) {}
