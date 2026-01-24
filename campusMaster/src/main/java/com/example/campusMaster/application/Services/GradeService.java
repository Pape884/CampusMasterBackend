package com.example.campusMaster.application.Services;
import com.example.campusMaster.application.dto.request.CreateGradeRequest;
import com.example.campusMaster.application.dto.response.GradeResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.domain.entity.Grade;
import com.example.campusMaster.domain.entity.Submission;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.persistence.repository.GradeRepository;
import com.example.campusMaster.infrastructure.persistence.repository.SubmissionRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {
    
    private final GradeRepository gradeRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    
    /**
     * Noter une soumission
     */
    public GradeResponse gradeSubmission(Long submissionId, Long teacherId, CreateGradeRequest request) {
        // Charger la soumission
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Soumission introuvable"));
        
        // Charger l'enseignant
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        
        // Vérifier si une note existe déjà pour cette soumission
        if (gradeRepository.findBySubmission(submission).isPresent()) {
            throw new RuntimeException("Cette soumission a déjà été notée");
        }
        
        // Créer la note
        Grade grade = Grade.builder()
                .submission(submission)
                .gradedBy(teacher)
                .points(request.points())
                .feedback(request.feedback())
                .build();
        
        Grade saved = gradeRepository.save(grade);
        return mapToResponse(saved);
    }
    
    /**
     * Mettre à jour une note
     */
    public GradeResponse updateGrade(Long gradeId, CreateGradeRequest request) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
        
        grade.setPoints(request.points());
        grade.setFeedback(request.feedback());
        
        Grade updated = gradeRepository.save(grade);
        return mapToResponse(updated);
    }
    
    /**
     * Récupérer une note par ID
     */
    @Transactional(readOnly = true)
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));
        return mapToResponse(grade);
    }
    
    /**
     * Récupérer toutes les notes d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer toutes les notes d'un cours
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByCourse(Long courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculer la moyenne d'un étudiant
     */
    @Transactional(readOnly = true)
    public Double getStudentAverage(Long studentId) {
        Double average = gradeRepository.getAverageGradeByStudentId(studentId);
        return average != null ? average : 0.0;
    }
    
    /**
     * Calculer la moyenne d'un devoir
     */
    @Transactional(readOnly = true)
    public Double getAssignmentAverage(Long assignmentId) {
        Double average = gradeRepository.getAverageGradeByAssignmentId(assignmentId);
        return average != null ? average : 0.0;
    }
    
    /**
     * Récupérer toutes les notes d'un enseignant
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        
        return gradeRepository.findByGradedBy(teacher).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprimer une note
     */
    public void deleteGrade(Long gradeId) {
        if (!gradeRepository.existsById(gradeId)) {
            throw new RuntimeException("Note introuvable");
        }
        gradeRepository.deleteById(gradeId);
    }
     // ==================== MAPPER ====================
    
    private GradeResponse mapToResponse(Grade grade) {
        UserResponse gradedBy = new UserResponse(
                grade.getGradedBy().getId(),
                grade.getGradedBy().getPrenom(),
                grade.getGradedBy().getNom(),
                grade.getGradedBy().getEmail(),
                grade.getGradedBy().getRole(),
                grade.getGradedBy().getIsActive()
        );
        
        return new GradeResponse(
                grade.getId(),
                grade.getPoints(),
                grade.getFeedback(),
                grade.getGradedAt(),
                gradedBy
        );
    }
}