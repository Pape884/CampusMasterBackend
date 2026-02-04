package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.response.enrollement.EnrollmentResponse;
import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Enrollment;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.EnrollementRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ModuleRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollementRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    /**
     * Inscrire un utilisateur à un cours
     */
    public EnrollmentResponse enrollUser(Long userId, Long moduleId) {
        // Charger l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Charger le module
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        // Vérifier si l'utilisateur n'est pas déjà inscrit
        if (enrollmentRepository.existsByUserAndModule(user, module)) {
            throw new RuntimeException("Vous êtes déjà inscrit à ce cours");
        }

        // Créer l'inscription
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .module(module)
                .isActive(true)
                .build();

        enrollment.enroll();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    /**
     * Désinscrire un étudiant d'un cours
     */
    public void unenrollStudent(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        if (!enrollment.getIsActive()) {
            throw new RuntimeException("Cette inscription est déjà inactive");
        }

        enrollment.unenroll();
        enrollmentRepository.save(enrollment);
    }

    /**
     * Récupérer une inscription par ID
     */
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        return mapToResponse(enrollment);
    }

    /**
     * Récupérer toutes les inscriptions d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByUser(Long userId) {
        return enrollmentRepository.findActiveEnrollmentsByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les inscriptions d'un module
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByModule(Long moduleId) {
        return enrollmentRepository.findActiveEnrollmentsByModuleId(moduleId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Compter le nombre d'inscriptions actives pour un module
     */
    @Transactional(readOnly = true)
    public Long countEnrollmentsByModule(Long moduleId) {
        // Vérifier que le module existe
        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        return enrollmentRepository.countActiveEnrollmentsByModuleId(moduleId);
    }

    /**
     * Vérifier si un user est inscrit à un module
     */
    @Transactional(readOnly = true)
    public boolean isUserEnrolled(Long userId, Long moduleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        return enrollmentRepository.existsByUserAndModule(user, module);
    }

    /**
     * Mettre à jour la note finale d'un étudiant
     */
    public EnrollmentResponse updateFinalGrade(Long enrollmentId, Double grade) {
        if (grade < 0 || grade > 20) {
            throw new RuntimeException("La note doit être entre 0 et 20");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        enrollment.setFinalGrade(grade);
        Enrollment updated = enrollmentRepository.save(enrollment);

        return mapToResponse(updated);
    }

    public List<ModuleResponse> getModuleByTeacher(Long teacherId) {

        // Vérifier que l'enseignant existe
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable"));

        // (Optionnel mais recommandé) Vérifier le rôle
        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un enseignant");
        }

        // Récupérer les enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(teacherId);

        // Mapper vers ModuleResponse (sans doublons)
        return enrollments.stream()
                .map(Enrollment::getModule)
                .distinct()
                .map(this::mapToModule)
                .toList();
    }

    /**
     * Supprimer une inscription
     */
    public void deleteEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new RuntimeException("Inscription introuvable");
        }
        enrollmentRepository.deleteById(enrollmentId);
    }

    // ==================== MAPPER ====================

    private ModuleResponse mapToModule(CourseModule module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .code(module.getCode())
                .name(module.getName())
                .semestre(module.getSemestre())
                .build();
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .module(mapToModule(enrollment.getModule()))
                .enrolledAt(enrollment.getEnrolledAt())
                .isActive(enrollment.getIsActive())
                .finalGrade(enrollment.getFinalGrade())
                .build();

    }
}