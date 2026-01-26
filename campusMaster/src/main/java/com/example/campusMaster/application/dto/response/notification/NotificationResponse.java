package com.example.campusMaster.application.dto.response.notification;

import java.time.LocalDateTime;

import com.example.campusMaster.domain.enums.NotificationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    Long id;
    NotificationType notificationType;
    String title;
    String content;
    Boolean isRead;
    LocalDateTime createdAt;
}
