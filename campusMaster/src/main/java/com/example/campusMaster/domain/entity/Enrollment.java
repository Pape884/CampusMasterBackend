package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "module_id"})
    },
    indexes = {
        @Index(name = "idx_enrollment_user", columnList = "user_id"),
        @Index(name = "idx_enrollment_module", columnList = "module_id")
    }
)
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private Double finalGrade;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;
    
    // Méthodes métier
    public void enroll() {
        this.isActive = true;
        this.enrolledAt = LocalDateTime.now();
    }
    
    public void unenroll() {
        this.isActive = false;
    }
}