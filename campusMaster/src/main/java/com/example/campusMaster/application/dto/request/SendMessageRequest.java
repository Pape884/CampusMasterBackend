package com.example.campusMaster.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
    @NotNull(message = "Receiver ID obligatoire")
    Long receiverId,
    
    String subject,
    
    @NotBlank(message = "Contenu obligatoire")
    String content,
    
    String tags
) {}
