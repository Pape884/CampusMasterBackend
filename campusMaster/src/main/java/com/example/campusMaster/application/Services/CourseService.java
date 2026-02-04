package com.example.campusMaster.application.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campusMaster.application.dto.request.courses.CreateCourseRequest;
import com.example.campusMaster.application.dto.request.courses.UpdateCourseRequest;
import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.infrastructure.exception.ResourceAlreadyExistsException;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ModuleRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request, Long teacherId) {

        // Vérifier unicité du code
        if (courseRepository.existsByCode(request.code())) {
            throw new ResourceAlreadyExistsException("Code cours déjà utilisé");
        }

        // Vérifier enseignant
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable"));

        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un enseignant");
        }

        // Vérifier module
        CourseModule module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));

        // Déterminer le statut
        boolean isActive = "published".equalsIgnoreCase(request.status());

        Course course = Course.builder()
                .code(request.code())
                .titre(request.titre())
                .description(request.description())
                .credits(request.credits())
                .status(request.status())
                .teacher(teacher)
                .module(module)
                .isActive(isActive)
                .build();

        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }

    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        course.setTitre(request.titre());
        course.setDescription(request.description());

        if (request.isActive() != null) {
            course.setIsActive(request.isActive());
        }

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
        return mapToResponse(course);
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));

        // Selectionner les chapitres du courset les supprimer


        courseRepository.delete(course);
    }

    public List<CourseResponse> getCoursesByModule(Long moduleId) {
        return courseRepository.findByModuleId(moduleId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Mapper
    private CourseResponse mapToResponse(Course course) {

        UserResponse teacher = UserResponse.builder()
                .id(course.getTeacher().getId())
                .matricule(course.getTeacher().getMatricule())
                .prenom(course.getTeacher().getPrenom())
                .nom(course.getTeacher().getNom())
                .email(course.getTeacher().getEmail())
                .telephone(course.getTeacher().getTelephone())
                .role(course.getTeacher().getRole())
                .isActive(course.getTeacher().getIsActive())
                .build();

        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .titre(course.getTitre())
                .description(course.getDescription())
                .moduleId(course.getModule().getId())
                .isActive(course.getIsActive())
                .teacherId(course.getTeacher().getId())
                .createdAt(course.getCreatedAt())
                .build();
    }

}
