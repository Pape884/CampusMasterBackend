package com.example.campusMaster.application.dto.response.chapter;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ChapterResponse {
    Long id;
    String title;
    String description;
    String content;
    Integer order;
    Long courseId;
}
