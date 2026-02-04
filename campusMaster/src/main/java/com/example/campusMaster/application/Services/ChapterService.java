package com.example.campusMaster.application.Services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.campusMaster.application.dto.request.chapter.ChapterOrderRequest;
import com.example.campusMaster.application.dto.request.chapter.ChapterRequest;
import com.example.campusMaster.application.dto.response.chapter.ChapterResponse;
import com.example.campusMaster.domain.entity.Chapter;
import com.example.campusMaster.domain.entity.ChapterResource;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.enums.ResourceType;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.ChapterRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ChapterResourceRepository;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {

        private final ChapterRepository chapterRepository;
        private final CourseRepository courseRepository;
        private final ChapterResourceRepository resourceRepository;
        private final FileStorageService fileStorageService;

        /* ===== Get ===== */

        public List<ChapterResponse> getChaptersByCourse(Long courseId) {
                return chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        /* ======= Get One Chapter ===== */
        public ChapterResponse getChapterById(Long id) {
                Chapter chapter = chapterRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));
                return mapToResponse(chapter);
        }

        /* ===== Create ===== */
        public ChapterResponse createChapter(ChapterRequest request) {
                Course course = courseRepository.findById(request.getCourseId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

                Chapter chapter = Chapter.builder()
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .content(request.getContent())
                                .orderIndex(request.getOrder())
                                .course(course)
                                .build();
                Chapter savedChapter = chapterRepository.save(chapter);

                return mapToResponse(savedChapter);
        }

        /* ===== Update ===== */
        public ChapterResponse updateChapter(Long id, ChapterRequest request) {
                Chapter chapter = chapterRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

                chapter.setTitle(request.getTitle());
                chapter.setDescription(request.getDescription());
                chapter.setContent(request.getContent());

                Chapter updatedChapter = chapterRepository.save(chapter);
                return mapToResponse(updatedChapter);
        }

        /* ===== Delete ===== */
        public void deleteChapter(Long id) {
                chapterRepository.deleteById(id);
        }

        /* ===== Update order ===== */
        public void updateChaptersOrder(Long courseId, List<ChapterOrderRequest> orders) {
                List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

                Map<Long, Integer> orderMap = orders.stream()
                                .collect(Collectors.toMap(ChapterOrderRequest::getId, ChapterOrderRequest::getOrder));

                chapters.forEach(ch -> ch.setOrderIndex(orderMap.get(ch.getId())));
        }

        /* ===== Add resource ===== */
        @Transactional
        public ChapterResource addResource(
                        Long chapterId,
                        String name,
                        ResourceType type,
                        MultipartFile file,
                        String url) {
                Chapter chapter = chapterRepository.findById(chapterId)
                                .orElseThrow(() -> new ResourceNotFoundException("Chapitre introuvable"));

                String resourceUrl;
                String size = null;

                if (file != null && !file.isEmpty()) {
                        resourceUrl = fileStorageService.uploadFile(file, "chapters");
                        size = file.getSize() + " bytes";
                } else if (url != null && !url.isBlank()) {
                        resourceUrl = url;
                } else {
                        throw new IllegalArgumentException("Un fichier ou une URL doit être fourni");
                }

                ChapterResource resource = ChapterResource.builder()
                                .name(name)
                                .type(type)
                                .url(resourceUrl)
                                .size(size)
                                .chapter(chapter)
                                .build();

                return resourceRepository.save(resource);
        }

        /* ===== Get Chapter resources ===== */
        public List<ChapterResource> getChapterResources(Long chapterId) {
                if (!chapterRepository.existsById(chapterId)) {
                        throw new ResourceNotFoundException("Chapitre introuvable");
                }
                return resourceRepository.findByChapterId(chapterId);
        }

        private ChapterResponse mapToResponse(Chapter chapter) {
                return ChapterResponse.builder()
                                .id(chapter.getId())
                                .content(chapter.getContent())
                                .title(chapter.getTitle())
                                .order(chapter.getOrderIndex())
                                .courseId(chapter.getCourse().getId())
                                .build();

        }
}
