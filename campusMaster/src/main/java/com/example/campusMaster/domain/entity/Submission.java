package com.example.campusMaster.domain.entity;
import com.example.campusMaster.domain.enums.SubmissionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", indexes = {
    @Index(name = "idx_submission_assignment", columnList = "assignment_id"),
    @Index(name = "idx_submission_student", columnList = "student_id"),
    @Index(name = "idx_submission_status", columnList = "submission_status")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String fileUrl;
    
    @Column(nullable = false, length = 255)
    private String fileName;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer version = 1;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private SubmissionStatus submissionStatus = SubmissionStatus.DRAFT;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column
    private Double grade;

    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    //@OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
    //private Grade grade;
    
    // Méthodes métier
    public void submit() {
        this.submissionStatus = SubmissionStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }
    
    public void updateVersion() {
        this.version++;
    }
    
    public void withdraw() {
        this.submissionStatus = SubmissionStatus.DRAFT;
    }
    
    public boolean isLate() {
        return submittedAt.isAfter(assignment.getDeadline());
    }
    
    // Méthodes utilitaires
    //public boolean isGraded() {
     //   return submissionStatus == SubmissionStatus.GRADED && grade != null;
    //}

}
