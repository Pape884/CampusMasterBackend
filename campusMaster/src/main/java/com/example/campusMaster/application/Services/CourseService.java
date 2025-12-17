package com.example.campusMaster.application.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campusMaster.application.dto.request.CreateCourseRequest;
import com.example.campusMaster.application.dto.request.UpdateCourseRequest;
import com.example.campusMaster.application.dto.response.CourseResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.entity.CourseModule;
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
    
    public CourseResponse createCourse(CreateCourseRequest request, Long teacherId) {
        // Vérifier si le code existe déjà
        if (courseRepository.existsByCode(request.code())) {
            throw new RuntimeException("Code cours déjà utilisé");
        }
        
        User teacher = userRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        
         CourseModule module = null;
        if (request.moduleId() != null) {
            module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new RuntimeException("Module introuvable"));
        }
        
        Course course = Course.builder()
            .code(request.code())
            .titre(request.titre())
            .description(request.description())
            .semestre(request.semestre())
            .annee(request.annee())
            .teacher(teacher)
            .module(module)
            .isActive(true)
            .build();
        
        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }
    
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        
        course.setTitre(request.titre());
        course.setDescription(request.description());
        
        if (request.semestre() != null) {
            course.setSemestre(request.semestre());
        }
        if (request.annee() != null) {
            course.setAnnee(request.annee());
        }
        if (request.isActive() != null) {
            course.setIsActive(request.isActive());
        }
        
        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }
    
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        return mapToResponse(course);
    }
    
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findActiveCoursesbyTeacher(teacherId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<CourseResponse> getCoursesBySemestre(String semestre) {
        return courseRepository.findBySemestre(semestre).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        courseRepository.delete(course);
    }
    
    // Mapper
    private CourseResponse mapToResponse(Course course) {
        UserResponse teacher = new UserResponse(
            course.getTeacher().getId(),
            course.getTeacher().getPrenom(),
            course.getTeacher().getNom(),
            course.getTeacher().getEmail(),
            course.getTeacher().getRole(),
            course.getTeacher().getIsActive()
        );
        
        return new CourseResponse(
            course.getId(),
            course.getCode(),
            course.getTitre(),
            course.getDescription(),
            course.getSemestre(),
            course.getAnnee(),
            course.getIsActive(),
            teacher,
            course.getEnrolledStudentsCount(),
            course.getCreatedAt()
        );
    }

}
