package com.example.campusMaster.application.dto.request.chapter;

import lombok.Data;

@Data
public class ChapterRequest {
    private String title;
    private String description;
    private String content;
    private Integer order;
    private Long courseId;
}

