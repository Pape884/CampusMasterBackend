package com.example.campusMaster.application.dto.request.soumissions;

import jakarta.validation.constraints.NotBlank;

public record CreateSubmissionRequest(
    @NotBlank(message = "File URL obligatoire")
    String fileUrl,
    
    @NotBlank(message = "File name obligatoire")
    String fileName,
    
    String comments
) {}
