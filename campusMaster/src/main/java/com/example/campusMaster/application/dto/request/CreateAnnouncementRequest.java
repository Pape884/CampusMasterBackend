package com.example.campusMaster.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnnouncementRequest(
    @NotNull(message = "Course ID obligatoire")
    Long courseId,
    
    @NotBlank(message = "Titre obligatoire")
    String title,
    
    @NotBlank(message = "Contenu obligatoire")
    String content,
    
    Boolean isPinned,
    
    LocalDateTime expiresAt
) {}
