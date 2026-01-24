package com.example.campusMaster.application.dto.request.assignments;

import java.time.LocalDateTime;

public record UpdateAssignmentRequest(
    String description,
    LocalDateTime deadline
) {}
