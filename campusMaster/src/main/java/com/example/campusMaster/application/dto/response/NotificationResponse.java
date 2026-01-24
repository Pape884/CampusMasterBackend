package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

import com.example.campusMaster.domain.enums.NotificationType;

public record NotificationResponse(
    Long id,
    NotificationType notificationType,
    String title,
    String content,
    Boolean isRead,
    LocalDateTime createdAt
) {}
