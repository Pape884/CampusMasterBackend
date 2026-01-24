package com.example.campusMaster.application.dto.response.submissions;

import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.enums.SubmissionStatus;

public record SubmissionResponse(
    Long id,
    String fileUrl,
    String fileName,
    Integer version,
    SubmissionStatus submissionStatus,
    String comments,
    LocalDateTime submittedAt,
    LocalDateTime uploadedAt,
    //GradeResponse grade,
    UserResponse student
) {}
