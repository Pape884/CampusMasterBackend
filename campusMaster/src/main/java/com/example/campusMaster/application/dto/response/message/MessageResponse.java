package com.example.campusMaster.application.dto.response.message;

import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;

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
