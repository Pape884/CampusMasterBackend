package com.example.campusMaster.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Announcement;
import com.example.campusMaster.domain.entity.Course;

@Repository
public interface AnnouncementRepository extends JpaRepository <Announcement, Long> {
     List<Announcement> findByCourse(Course course);
    
    List<Announcement> findByIsPinned(Boolean isPinned);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId ORDER BY a.isPinned DESC, a.publishedAt DESC")
    List<Announcement> findByCourseIdOrderByPinnedAndDate(Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND (a.expiresAt IS NULL OR a.expiresAt > :now)")
    List<Announcement> findActiveByCourseId(Long courseId, LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.isPinned = true ORDER BY a.publishedAt DESC")
    List<Announcement> findAllPinned();

}
