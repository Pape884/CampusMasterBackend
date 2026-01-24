package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

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