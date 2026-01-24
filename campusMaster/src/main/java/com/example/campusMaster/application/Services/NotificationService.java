package com.example.campusMaster.application.Services;


import com.example.campusMaster.application.dto.request.CreateNotificationRequest;
import com.example.campusMaster.application.dto.response.NotificationResponse;
import com.example.campusMaster.domain.entity.Notification;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.NotificationType;
import com.example.campusMaster.infrastructure.persistence.repository.NotificationRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    /**
     * Créer une notification
     */
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        // Charger l'utilisateur
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        // Créer la notification
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(request.notificationType())
                .title(request.title())
                .content(request.content())
                .isRead(false)
                .build();
        
        notification.create();
        
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }
    
    /**
     * Créer une notification (méthode simplifiée)
     */
    public NotificationResponse createNotification(Long userId, NotificationType type, String title, String content) {
        // Charger l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        // Créer la notification
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(type)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        
        notification.create();
        
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }
    
    /**
     * Récupérer une notification par ID
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        return mapToResponse(notification);
    }
    
    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer toutes les notifications non lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Compter le nombre de notifications non lues
     */
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    /**
     * Récupérer les notifications par type
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByNotificationType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        
        if (notification.getIsRead()) {
            throw new RuntimeException("La notification est déjà marquée comme lue");
        }
        
        notification.markAsRead();
        notificationRepository.save(notification);
    }
    
    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);
        
        if (unreadNotifications.isEmpty()) {
            throw new RuntimeException("Aucune notification non lue");
        }
        
        unreadNotifications.forEach(notification -> {
            notification.markAsRead();
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification introuvable");
        }
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Supprimer toutes les notifications lues d'un utilisateur
     */
    public void deleteReadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        notificationRepository.deleteByUserAndIsRead(user, true);
    }
    
    /**
     * Supprimer toutes les notifications d'un utilisateur
     */
    public void deleteAllUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        List<Notification> notifications = notificationRepository.findByUser(user);
        
        if (notifications.isEmpty()) {
            throw new RuntimeException("Aucune notification à supprimer");
        }
        
        notificationRepository.deleteAll(notifications);
    }
    
    // ==================== MAPPER ====================
    
    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getContent(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
