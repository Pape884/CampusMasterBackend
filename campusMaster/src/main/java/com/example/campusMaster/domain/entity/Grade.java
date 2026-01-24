package com.example.campusMaster.domain.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades", indexes = {
    @Index(name = "idx_grade_submission", columnList = "submission_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Points obligatoire")
    @Column(nullable = false)
    private Double points;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime gradedAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relations
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", nullable = false)
    private User gradedBy;
    
    // Méthodes métier
    public void assignGrade(Double points, String feedback) {
        this.points = points;
        this.feedback = feedback;
    }
    
    public void updateFeedback(String newFeedback) {
        this.feedback = newFeedback;
    }
    
    public double getPercentage() {
        // Calcul du pourcentage (nécessite maxPoints de Assignment)
        return 0.0; //Ici c'est à implémenter si on aura du temps 
    }
}
