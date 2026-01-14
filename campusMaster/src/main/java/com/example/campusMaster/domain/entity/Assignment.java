package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assignments", indexes = {
    @Index(name = "idx_assignment_course", columnList = "course_id"),
    @Index(name = "idx_assignment_deadline", columnList = "deadline")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Deadline obligatoire")
    @Column(nullable = false)
    private LocalDateTime deadline;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Submission> submissions = new HashSet<>();
    
    // Méthodes métier
    public void publish() {
        // Logique de publication
    }
    
    public void updateDeadline(LocalDateTime newDeadline) {
        this.deadline = newDeadline;
    }
    
    public boolean isDeadlinePassed() {
        return LocalDateTime.now().isAfter(deadline);
    }
    
    public void calculateStatistics() {
        // Calcul des statistiques
    }
}