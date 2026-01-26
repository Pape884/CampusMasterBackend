package com.example.campusMaster.application.dto.response.grade;

import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;

public record GradeResponse(
    Long id,
    Double points,
    String feedback,
    LocalDateTime gradedAt,
    UserResponse gradedBy
) {}
