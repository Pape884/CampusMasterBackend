package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.example.campusMaster.domain.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_type", columnList = "notification_type"),
    @Index(name = "idx_notification_read", columnList = "is_read")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType notificationType;
    
    @NotBlank(message = "Titre obligatoire")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Méthodes métier
    public void create() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    public void delete() {
        // Logique de suppression
    }
    
    // Méthodes utilitaires
    public boolean isUnread() {
        return !isRead;
    }
}