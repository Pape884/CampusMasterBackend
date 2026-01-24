package com.example.campusMaster.application.Services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.campusMaster.application.dto.request.courses.UpdateCourseDTO;
import com.example.campusMaster.application.dto.request.departments.RegisterDepartmentDTO;
import com.example.campusMaster.application.dto.request.departments.UpdateDepartmentDTO;
import com.example.campusMaster.application.dto.request.modules.UpdateModuleDTO;
import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.example.campusMaster.application.dto.response.departments.DepartmentResponse;
import com.example.campusMaster.application.dto.response.departments.DepartmentStats;
import com.example.campusMaster.application.dto.response.departments.DepartmentWithModuleResponse;
import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Department;
import com.example.campusMaster.infrastructure.exception.ResourceAlreadyExistsException;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.CourseRepository;
import com.example.campusMaster.infrastructure.persistence.repository.DepartmentRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ModuleRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public DepartmentResponse createDepartment(RegisterDepartmentDTO dto) {

        // Vérifier département
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new ResourceAlreadyExistsException("Department code already exists");
        }

        // Vérifier modules AVANT sauvegarde
        if (dto.getModules() != null) {
            dto.getModules().forEach(m -> {
                if (moduleRepository.existsByCode(m.getCode())) {
                    throw new ResourceAlreadyExistsException(
                            "Module code already exists: " + m.getCode());
                }
            });
        }

        // Créer département
        Department department = Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .modules(new HashSet<>())
                .build();

        Department savedDepartment = departmentRepository.save(department);

        // Créer modules
        if (dto.getModules() != null && !dto.getModules().isEmpty()) {

            Set<CourseModule> modules = dto.getModules().stream()
                    .map(m -> CourseModule.builder()
                            .name(m.getName())
                            .code(m.getCode())
                            .semestre(m.getSemestre())
                            .department(savedDepartment)
                            .build())
                    .collect(Collectors.toSet());

            moduleRepository.saveAll(modules);
            savedDepartment.setModules(modules);
        }

        // Retourner un DTO (PAS l’entity)
        return mapToResponse(savedDepartment);
    }

    public DepartmentWithModuleResponse getDepartmentById(Long id) {

        Department department = departmentRepository
                .findByIdWithModulesAndCourses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return mapDepartment(department);
    }

    public Page<Department> getAllDepartments(
            String search,
            Boolean isActive,
            int page,
            int limit,
            String sortBy,
            String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, sort);

        Specification<Department> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (search != null && !search.isEmpty()) {
                String p = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("name")), p),
                                cb.like(cb.lower(root.get("code")), p),
                                cb.like(cb.lower(root.get("description")), p)));
            }

            if (isActive != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        return departmentRepository.findAll(spec, pageable);
    }

    public CourseModule getModuleByDepartment(Long departmentId) {
        return moduleRepository.findByDepartmentId(departmentId).stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public DepartmentWithModuleResponse updateDepartment(Long id, UpdateDepartmentDTO dto) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable"));

        // Update département
        department.update(dto.getName(), dto.getDescription(), dto.getCode());

        // Synchronisation des modules
        Map<Long, CourseModule> existingModules = department.getModules()
                .stream()
                .filter(m -> m.getId() != null)
                .collect(Collectors.toMap(CourseModule::getId, m -> m));

        Set<CourseModule> modules = department.getModules();
        modules.clear();

        for (UpdateModuleDTO moduleDTO : dto.getModules()) {

            CourseModule module;

            if (moduleDTO.getId() != null) {
                module = moduleRepository.findById(moduleDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));
            } else {
                module = new CourseModule();
                module.setDepartment(department);
            }

            module.setName(moduleDTO.getName());
            module.setCode(moduleDTO.getCode());
            module.setSemestre(moduleDTO.getSemestre());

            syncCourses(module, moduleDTO.getCourses());

            modules.add(module);
        }

        department.setModules(modules);

        departmentRepository.save(department);

        return mapDepartment(department);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Département introuvable");
        }
        departmentRepository.deleteById(id);
    }

    //Changer le status du departement {status: true || false}
    public DepartmentWithModuleResponse patchDepartment(Long id, boolean status) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable"));

        department.setIsActive(status);

        return mapDepartment(departmentRepository.save(department));

    }

    public DepartmentStats getDepartmentStats() {

        long total = departmentRepository.count();
        long active = departmentRepository.countByIsActive(true);
        long inactive = departmentRepository.countByIsActive(false);
        long modules = departmentRepository.countAllModules();

        return DepartmentStats.builder()
                .totalDepartments(total)
                .activeDepartments(active)
                .inactiveDepartments(inactive)
                .totalModules(modules)
                .build();
    }

    public DepartmentResponse mapToResponse(Department department) {

        Long id = department.getId();

        return DepartmentResponse.builder()
                .id(id)
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .isActive(department.getIsActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .studentsCount(departmentRepository.countStudents(id))
                .teachersCount(departmentRepository.countTeachers(id))
                .modulesCount(moduleRepository.countByDepartmentId(id))
                .build();
    }

    private DepartmentWithModuleResponse mapDepartment(Department d) {

        return DepartmentWithModuleResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .modules(
                        d.getModules().stream()
                                .map(this::mapModule)
                                .toList())
                .build();
    }

    private ModuleResponse mapModule(CourseModule m) {

        return ModuleResponse.builder()
                .id(m.getId())
                .code(m.getCode())
                .name(m.getName())
                .semestre(m.getSemestre())
                .courses(
                        m.getCourses().stream()
                                .map(this::mapCourse)
                                .toList())
                .build();
    }

    private CourseResponse mapCourse(Course c) {

        return CourseResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .titre(c.getTitre())
                .semestre(c.getSemestre())
                .annee(c.getAnnee())
                .build();
    }

    private void syncCourses(CourseModule module, List<UpdateCourseDTO> courseDTOs) {

        if (courseDTOs == null)
            return;

        Map<Long, Course> existingCourses = module.getCourses()
                .stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Course::getId, c -> c));

        Set<Course> courses = module.getCourses();
        courses.clear();

        for (UpdateCourseDTO dto : courseDTOs) {
            Course course;

            if (dto.getId() != null) {
                course = courseRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable"));
            } else {
                course = new Course();
                course.setModule(module);
            }

            course.setTitre(dto.getTitre());
            course.setDescription(dto.getDescription());
            course.setAnnee(dto.getAnnee());
            course.setIsActive(dto.getIsActive());

            courses.add(course);
        }
        module.setCourses(courses);
    }

}
