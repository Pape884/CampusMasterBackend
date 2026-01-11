package com.example.campusMaster.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateAssignmentRequest(
    @NotBlank(message = "Course ID obligatoire")
    String courseId,
    
    String description,
    
    @NotNull(message = "Deadline obligatoire")
    LocalDateTime deadline
) {}
