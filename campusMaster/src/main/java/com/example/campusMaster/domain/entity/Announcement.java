package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements", indexes = {
    @Index(name = "idx_announcement_course", columnList = "course_id"),
    @Index(name = "idx_announcement_published", columnList = "published_at")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Titre obligatoire")
    @Column(nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "Contenu obligatoire")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isPinned = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    // Méthodes métier
    public void publish() {
        this.publishedAt = LocalDateTime.now();
    }
    
    public void pin() {
        this.isPinned = true;
    }
    
    public void edit(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    public void delete() {
        // Logique de suppression
    }
    
    // Méthodes utilitaires
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public void unpin() {
        this.isPinned = false;
    }
}
