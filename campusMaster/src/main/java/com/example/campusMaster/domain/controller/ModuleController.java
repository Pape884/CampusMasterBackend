package com.example.campusMaster.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.ModuleService;
import com.example.campusMaster.application.dto.request.CreateModuleRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.ModuleResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Modules", description = "Gestion des modules")
public class ModuleController {
    
    private final ModuleService moduleService;
    
    @Operation(summary = "Créer un module (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(
            @Valid @RequestBody CreateModuleRequest request
    ) {
        ModuleResponse module = moduleService.createModule(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(module, "Module créé"));
    }
    
    @Operation(summary = "Modifier un module (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description
    ) {
        ModuleResponse module = moduleService.updateModule(id, name, description);
        return ResponseEntity.ok(ApiResponse.success(module, "Module mis à jour"));
    }
    
    @Operation(summary = "Récupérer un module par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long id) {
        ModuleResponse module = moduleService.getModuleById(id);
        return ResponseEntity.ok(ApiResponse.success(module));
    }
    
    @Operation(summary = "Lister tous les modules")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllModules() {
        List<ModuleResponse> modules = moduleService.getAllModules();
        return ResponseEntity.ok(ApiResponse.success(modules));
    }
    
    @Operation(summary = "Lister les modules d'un département")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByDepartment(
            @PathVariable Long departmentId
    ) {
        List<ModuleResponse> modules = moduleService.getModulesByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(modules));
    }
    
    @Operation(summary = "Supprimer un module (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Module supprimé"));
    }
}
