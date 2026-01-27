package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.request.modules.RegisterModuleDTO;
import com.example.campusMaster.application.dto.request.modules.UpdateModuleDTO;
import com.example.campusMaster.application.dto.response.departments.DepartmentResponse;
import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Department;
import com.example.campusMaster.infrastructure.persistence.repository.DepartmentRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Créer un module
     */
    public ModuleResponse createModule(RegisterModuleDTO request) {
        // Vérifier si le code existe déjà
        if (moduleRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Un module avec ce code existe déjà");
        }

        // Charger le département
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));


        // Créer le module
        CourseModule module = CourseModule.builder()
                .code(request.getCode())
                .name(request.getName())
                .semestre(request.getSemestre())
                .department(department)
                .build();

        module.create();

        CourseModule saved = moduleRepository.save(module);
        return mapToResponse(saved);
    }

    /**
     * Mettre à jour un module
     */
    public ModuleResponse updateModule(Long moduleId, UpdateModuleDTO request) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        module.update(request.getName(), request.getCode(), request.getSemestre());

        CourseModule updated = moduleRepository.save(module);
        return mapToResponse(updated);
    }

    /**
     * Récupérer un module par ID
     */
    @Transactional(readOnly = true)
    public ModuleResponse getModuleById(Long id) {
        CourseModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));
        return mapToResponse(module);
    }


    /**
     * Récupérer tous les modules
     */
    @Transactional(readOnly = true)
    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les modules d'un département
     */
    @Transactional(readOnly = true)
    public List<ModuleResponse> getModulesByDepartment(Long departmentId) {
        // Vérifier que le département existe
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        return moduleRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les modules d'un semestre
     */
    /*@Transactional(readOnly = true)
    public List<ModuleResponse> getModulesBySemestre(String semestre) {

        return moduleRepository.findBySemestre(semestre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }*/

    /**
     * Vérifier si un code de module existe
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return moduleRepository.existsByCode(code);
    }

    /**
     * Changer le département d'un module
     */
    public ModuleResponse changeDepartment(Long moduleId, Long newDepartmentId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        Department newDepartment = departmentRepository.findById(newDepartmentId)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        module.setDepartment(newDepartment);

        CourseModule updated = moduleRepository.save(module);
        return mapToResponse(updated);
    }

    

    /**
     * Supprimer un module
     */
    public void deleteModule(Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        // Vérifier si le module a des cours associés
        if (module.getCoursesCount() > 0) {
            throw new RuntimeException("Impossible de supprimer un module ayant des cours associés");
        }

        moduleRepository.deleteById(moduleId);
    }

    /**
     * Compter le nombre de cours d'un module
     */
    @Transactional(readOnly = true)
    public Integer countCoursesByModule(Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        return module.getCoursesCount();
    }

    // ==================== MAPPER ====================

    private ModuleResponse mapToResponse(CourseModule module) {

        DepartmentResponse departmentResponse = null;

        if (module.getDepartment() != null) {
            departmentResponse = DepartmentResponse.builder()
                    .id(module.getDepartment().getId())
                    .name(module.getDepartment().getName())
                    .code(module.getDepartment().getCode())
                    .description(module.getDepartment().getDescription())
                    .isActive(module.getDepartment().getIsActive())
                    .createdAt(module.getDepartment().getCreatedAt())
                    .updatedAt(module.getDepartment().getUpdatedAt())
                    .build();
        }

        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .code(module.getCode())
                .departmentId(module.getDepartment().getId())
                .build();
    }

}