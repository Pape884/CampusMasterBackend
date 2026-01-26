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
import com.example.campusMaster.application.dto.request.modules.RegisterModuleDTO;
import com.example.campusMaster.application.dto.request.modules.UpdateModuleDTO;
import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;

import java.time.LocalDateTime;
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
    public ResponseEntity<ApiSuccessResponse<ModuleResponse>> createModule(
            @Valid @RequestBody RegisterModuleDTO request
    ) {
        ModuleResponse module = moduleService.createModule(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.<ModuleResponse>builder()
                        .success(true)
                        .message("Module créé")
                        .data(module)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @Operation(summary = "Modifier un module (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<ModuleResponse>> updateModule(
            @PathVariable Long id,
            UpdateModuleDTO request
    ) {
        ModuleResponse module = moduleService.updateModule(id, request);
        return ResponseEntity.ok(ApiSuccessResponse.<ModuleResponse>builder()
                .success(true)
                .message("Module mis à jour")
                .data(module)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Récupérer un module par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<ModuleResponse>> getModuleById(@PathVariable Long id) {
        ModuleResponse module = moduleService.getModuleById(id);
        return ResponseEntity.ok(ApiSuccessResponse.<ModuleResponse>builder()
                .success(true)
                .message("Module récupéré")
                .data(module)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister tous les modules")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<ModuleResponse>>> getAllModules() {
        List<ModuleResponse> modules = moduleService.getAllModules();
        return ResponseEntity.ok(ApiSuccessResponse.<List<ModuleResponse>>builder()
                .success(true)
                .message("Modules récupérés")
                .data(modules)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister les modules d'un département")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiSuccessResponse<List<ModuleResponse>>> getModulesByDepartment(
            @PathVariable Long departmentId
    ) {
        List<ModuleResponse> modules = moduleService.getModulesByDepartment(departmentId);
        return ResponseEntity.ok(ApiSuccessResponse.<List<ModuleResponse>>builder()
                .success(true)
                .message("Modules récupérés")
                .data(modules)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Supprimer un module (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Module supprimé")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
