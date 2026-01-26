package com.example.campusMaster.application.dto.response.announcement;

import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;

public record AnnouncementResponse(
    Long id,
    String title,
    String content,
    Boolean isPinned,
    LocalDateTime publishedAt,
    LocalDateTime expiresAt,
    UserResponse author,
    Long courseId
) {}