package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.request.CreateModuleRequest;
import com.example.campusMaster.application.dto.response.DepartmentResponse;
import com.example.campusMaster.application.dto.response.ModuleResponse;
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
    public ModuleResponse createModule(CreateModuleRequest request) {
        // Vérifier si le code existe déjà
        if (moduleRepository.existsByCode(request.code())) {
            throw new RuntimeException("Un module avec ce code existe déjà");
        }
        
        // Charger le département
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));
        
        // Vérifier que le semestre est valide (entre 1 et 10 généralement)
        if (request.semestre() < 1 || request.semestre() > 10) {
            throw new RuntimeException("Le semestre doit être entre 1 et 10");
        }
        
        // Créer le module
        CourseModule module = CourseModule.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .semestre(request.semestre())
                .department(department)
                .build();
        
        module.create();
        
        CourseModule saved = moduleRepository.save(module);
        return mapToResponse(saved);
    }
    
    /**
     * Mettre à jour un module
     */
    public ModuleResponse updateModule(Long moduleId, String name, String description) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));
        
        module.update(name, description);
        
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
     * Récupérer un module par code
     */
    @Transactional(readOnly = true)
    public ModuleResponse getModuleByCode(String code) {
        CourseModule module = moduleRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Module introuvable avec le code: " + code));
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
    @Transactional(readOnly = true)
    public List<ModuleResponse> getModulesBySemestre(Integer semestre) {
        if (semestre < 1 || semestre > 10) {
            throw new RuntimeException("Le semestre doit être entre 1 et 10");
        }
        
        return moduleRepository.findBySemestre(semestre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
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
     * Changer le semestre d'un module
     */
    public ModuleResponse changeSemestre(Long moduleId, Integer newSemestre) {
        if (newSemestre < 1 || newSemestre > 10) {
            throw new RuntimeException("Le semestre doit être entre 1 et 10");
        }
        
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));
        
        module.setSemestre(newSemestre);
        
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
            departmentResponse = new DepartmentResponse(
                    module.getDepartment().getId(),
                    module.getDepartment().getName(),
                    module.getDepartment().getCode(),
                    module.getDepartment().getDescription()
            );
        }
        
        return new ModuleResponse(
                module.getId(),
                module.getCode(),
                module.getName(),
                module.getDescription(),
                module.getSemestre(),
                departmentResponse,
                module.getCoursesCount()
        );
    }
}