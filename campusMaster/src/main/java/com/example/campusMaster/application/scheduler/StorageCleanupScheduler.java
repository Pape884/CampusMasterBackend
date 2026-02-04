package com.example.campusMaster.application.scheduler;

import com.example.campusMaster.application.Services.FileStorageService;
import com.example.campusMaster.domain.entity.Chapter;
import com.example.campusMaster.domain.entity.ChapterResource;
import com.example.campusMaster.infrastructure.persistence.repository.ChapterRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ChapterResourceRepository;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageCleanupScheduler {

    private final ChapterRepository chapterRepository;
    private final ChapterResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    /**
     * 🔁 Tous les jours à 02:00 du matin
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanOrphanData() {
        log.info("🧹 Début du nettoyage automatique...");

        cleanOrphanResources();
        cleanOrphanChapters();
        cleanOrphanFiles();

        log.info("✅ Nettoyage terminé");
    }

    /**
     * 🗑️ Ressources sans chapitre
     */
    private void cleanOrphanResources() {
        List<ChapterResource> orphanResources =
                resourceRepository.findByChapterIsNull();

        for (ChapterResource resource : orphanResources) {
            deleteResourceFile(resource);
            resourceRepository.delete(resource);
            log.info("❌ Ressource orpheline supprimée : {}", resource.getId());
        }
    }

    /**
     * 🗑️ Chapitres sans cours
     */
    private void cleanOrphanChapters() {
        List<Chapter> orphanChapters =
                chapterRepository.findByCourseIsNull();

        for (Chapter chapter : orphanChapters) {
            chapterRepository.delete(chapter);
            log.info("❌ Chapitre orphelin supprimé : {}", chapter.getId());
        }
    }

    /**
     * 🧼 Fichiers présents sur disque mais absents en base
     */
    private void cleanOrphanFiles() {
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            return;
        }

        try {
            Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String fileUrl = "/" + uploadPath.relativize(file).toString().replace("\\", "/");

                    boolean existsInDb = resourceRepository.existsByUrl(fileUrl);

                    if (!existsInDb) {
                        try {
                            Files.delete(file);
                            log.info("🗑️ Fichier orphelin supprimé : {}", fileUrl);
                        } catch (IOException e) {
                            log.error("Erreur suppression fichier {}", fileUrl, e);
                        }
                    }
                });

        } catch (IOException e) {
            log.error("Erreur parcours répertoire uploads", e);
        }
    }

    /**
     * Supprimer fichier physique si nécessaire
     */
    private void deleteResourceFile(ChapterResource resource) {
        if (resource.getUrl() != null && resource.getUrl().startsWith("/")) {
            try {
                fileStorageService.deleteFile(resource.getUrl());
            } catch (Exception e) {
                log.warn("Impossible de supprimer le fichier {}",
                        resource.getUrl());
            }
        }
    }
}

