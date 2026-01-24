package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_sender", columnList = "sender_id"),
    @Index(name = "idx_message_receiver", columnList = "receiver_id"),
    @Index(name = "idx_message_read", columnList = "is_read")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 200)
    private String subject;
    
    @NotBlank(message = "Contenu obligatoire")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column(length = 100)
    private String tags;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    // Méthodes métier
    public void send() {
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    public void reply(String content) {
        // Logique de réponse
    }
    
    public void addTag(String tag) {
        if (tags == null || tags.isEmpty()) {
            tags = tag;
        } else if (!hasTag(tag)) {
            tags += "," + tag;
        }
    }
    
    // Méthodes utilitaires
    public boolean hasTag(String tag) {
        if (tags == null) return false;
        return tags.contains(tag);
    }
}
