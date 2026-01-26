package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.example.campusMaster.application.dto.response.enrollement.EnrollmentResponse;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.Enrollment;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;
import com.example.campusMaster.infrastructure.persistence.repository.EnrollementRepository;
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
    private final CourseRepository courseRepository;

    /**
     * Inscrire un étudiant à un cours
     */
    public EnrollmentResponse enrollStudent(Long studentId, Long courseId) {
        // Charger l'étudiant
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        // Vérifier que c'est bien un étudiant
        if (!student.getRole().name().equals("STUDENT")) {
            throw new RuntimeException("Seuls les étudiants peuvent s'inscrire aux cours");
        }

        // Charger le cours
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        // Vérifier si l'étudiant n'est pas déjà inscrit
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Vous êtes déjà inscrit à ce cours");
        }


        // Créer l'inscription
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
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
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findActiveEnrollmentsByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les inscriptions d'un cours
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findActiveEnrollmentsByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Compter le nombre d'inscriptions actives pour un cours
     */
    @Transactional(readOnly = true)
    public Long countEnrollmentsByCourse(Long courseId) {
        // Vérifier que le cours existe
        courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        return enrollmentRepository.countActiveEnrollmentsByCourseId(courseId);
    }

    /**
     * Vérifier si un étudiant est inscrit à un cours
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        return enrollmentRepository.existsByStudentAndCourse(student, course);
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

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {

        CourseResponse courseResponse = CourseResponse.builder()
            .id(enrollment.getCourse().getId())
            .code(enrollment.getCourse().getCode())
            .titre(enrollment.getCourse().getTitre())
            .description(enrollment.getCourse().getDescription())
            .semestre(enrollment.getCourse().getSemestre())
            .annee(enrollment.getCourse().getAnnee())
            .isActive(enrollment.getCourse().getIsActive())
            .module(enrollment.getCourse().getModule())
            .credits(enrollment.getCourse().getCredits())
            .createdAt(enrollment.getCourse().getCreatedAt())
        .build();

        EnrollmentResponse enrollmentResponse = EnrollmentResponse.builder()
                .id(enrollment.getId())
                .course(courseResponse)
                .enrolledAt(enrollment.getEnrolledAt())
                .isActive(enrollment.getIsActive())
                .finalGrade(enrollment.getFinalGrade())
        .build();

        return enrollmentResponse;
    }
}