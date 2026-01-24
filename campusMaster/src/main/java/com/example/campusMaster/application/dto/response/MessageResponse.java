package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    UserResponse sender,
    UserResponse receiver,
    String subject,
    String content,
    Boolean isRead,
    String tags,
    LocalDateTime sentAt
) {}
