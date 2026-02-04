package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.campusMaster.domain.entity.ChapterResource;

public interface ChapterResourceRepository extends JpaRepository<ChapterResource, Long> {

    /* recuperer une ressource par chapitre */
    List<ChapterResource> findByChapterId(Long chapterId);

    List<ChapterResource> findByChapterIsNull();

    boolean existsByUrl(String url);

}
