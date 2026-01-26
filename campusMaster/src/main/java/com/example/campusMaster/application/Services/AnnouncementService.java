package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.request.CreateAnnouncementRequest;
import com.example.campusMaster.application.dto.response.AnnouncementResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.domain.entity.Announcement;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.persistence.repository.AnnouncementRepository;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    /**
     * Créer une annonce
     */
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request, Long authorId) {
        // Charger le cours
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        
        // Charger l'auteur
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Auteur introuvable"));
        
        // Vérifier que l'auteur est un enseignant ou admin
        if (!author.getRole().name().equals("TEACHER") && !author.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Seuls les enseignants et administrateurs peuvent créer des annonces");
        }
        
        // Créer l'annonce
        Announcement announcement = Announcement.builder()
                .course(course)
                .author(author)
                .title(request.title())
                .content(request.content())
                .isPinned(request.isPinned() != null ? request.isPinned() : false)
                .expiresAt(request.expiresAt())
                .build();
        
        announcement.publish();
        
        Announcement saved = announcementRepository.save(announcement);
        return mapToResponse(saved);
    }
    
    /**
     * Mettre à jour une annonce
     */
    public AnnouncementResponse updateAnnouncement(Long announcementId, String title, String content) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        
        // Vérifier si l'annonce n'est pas expirée
        if (announcement.isExpired()) {
            throw new RuntimeException("Impossible de modifier une annonce expirée");
        }
        
        announcement.edit(title, content);
        
        Announcement updated = announcementRepository.save(announcement);
        return mapToResponse(updated);
    }
    
    /**
     * Récupérer une annonce par ID
     */
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        return mapToResponse(announcement);
    }
    
    /**
     * Récupérer toutes les annonces d'un cours
     */
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByCourse(Long courseId) {
        return announcementRepository.findByCourseIdOrderByPinnedAndDate(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les annonces actives d'un cours (non expirées)
     */
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getActiveAnnouncementsByCourse(Long courseId) {
        return announcementRepository.findActiveByCourseId(courseId, LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer toutes les annonces épinglées
     */
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getPinnedAnnouncements() {
        return announcementRepository.findAllPinned().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Épingler ou désépingler une annonce
     */
    public void togglePin(Long announcementId, boolean pinned) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        
        if (pinned) {
            announcement.pin();
        } else {
            announcement.unpin();
        }
        
        announcementRepository.save(announcement);
    }
    
    /**
     * Épingler une annonce
     */
    public AnnouncementResponse pinAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        
        if (announcement.getIsPinned()) {
            throw new RuntimeException("L'annonce est déjà épinglée");
        }
        
        announcement.pin();
        Announcement updated = announcementRepository.save(announcement);
        
        return mapToResponse(updated);
    }
    
    /**
     * Désépingler une annonce
     */
    public AnnouncementResponse unpinAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        
        if (!announcement.getIsPinned()) {
            throw new RuntimeException("L'annonce n'est pas épinglée");
        }
        
        announcement.unpin();
        Announcement updated = announcementRepository.save(announcement);
        
        return mapToResponse(updated);
    }
    
    /**
     * Définir une date d'expiration
     */
    public AnnouncementResponse setExpirationDate(Long announcementId, LocalDateTime expiresAt) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date d'expiration doit être dans le futur");
        }
        
        announcement.setExpiresAt(expiresAt);
        Announcement updated = announcementRepository.save(announcement);
        
        return mapToResponse(updated);
    }
    
    /**
     * Supprimer une annonce
     */
    public void deleteAnnouncement(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new RuntimeException("Annonce introuvable");
        }
        announcementRepository.deleteById(announcementId);
    }
    
    /**
     * Supprimer toutes les annonces expirées d'un cours
     */
    public void deleteExpiredAnnouncements(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        
        List<Announcement> announcements = announcementRepository.findByCourse(course);
        List<Announcement> expiredAnnouncements = announcements.stream()
                .filter(Announcement::isExpired)
                .collect(Collectors.toList());
        
        if (expiredAnnouncements.isEmpty()) {
            throw new RuntimeException("Aucune annonce expirée à supprimer");
        }
        
        announcementRepository.deleteAll(expiredAnnouncements);
    }
    
    // ==================== MAPPER ====================
    
    private AnnouncementResponse mapToResponse(Announcement announcement) {
        UserResponse author = new UserResponse(
                announcement.getAuthor().getId(),
                announcement.getAuthor().getPrenom(),
                announcement.getAuthor().getNom(),
                announcement.getAuthor().getEmail(),
                announcement.getAuthor().getRole(),
                announcement.getAuthor().getIsActive()
        );
        
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getIsPinned(),
                announcement.getPublishedAt(),
                announcement.getExpiresAt(),
                author,
                announcement.getCourse().getId()
        );
    }
}
