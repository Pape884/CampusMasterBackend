package com.example.campusMaster.domain.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.campusMaster.application.Services.ChapterService;
import com.example.campusMaster.application.dto.request.chapter.ChapterOrderRequest;
import com.example.campusMaster.application.dto.request.chapter.ChapterRequest;
import com.example.campusMaster.application.dto.response.chapter.ChapterResponse;
import com.example.campusMaster.domain.entity.ChapterResource;
import com.example.campusMaster.domain.enums.ResourceType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("/course/{courseId}")
    public List<ChapterResponse> getByCourse(@PathVariable Long courseId) {
        return chapterService.getChaptersByCourse(courseId);
    }

    @GetMapping("/{id}")
    public ChapterResponse getChapterById(@PathVariable Long id) {
        return chapterService.getChapterById(id);
    }

    @PostMapping
    public ChapterResponse create(@RequestBody ChapterRequest request) {
        return chapterService.createChapter(request);
    }

    @PutMapping("/{id}")
    public ChapterResponse update(
            @PathVariable Long id,
            @RequestBody ChapterRequest request) {
        return chapterService.updateChapter(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chapterService.deleteChapter(id);
    }

    @PutMapping("/order/{courseId}")
    public void updateOrder(
            @PathVariable Long courseId,
            @RequestBody List<ChapterOrderRequest> orders) {
        chapterService.updateChaptersOrder(courseId, orders);
    }

    @PostMapping("/{chapterId}/resources")
    public ChapterResource addResource(
            @PathVariable Long chapterId,
            @RequestParam String name,
            @RequestParam ResourceType type,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String url) {
        return chapterService.addResource(chapterId, name, type, file, url);
    }

    @GetMapping("/{chapterId}/resources")
    public List<ChapterResource> getChapterResources(@PathVariable Long chapterId) {
        return chapterService.getChapterResources(chapterId);
    }
}
