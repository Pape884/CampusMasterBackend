package com.example.campusMaster.domain.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import com.example.campusMaster.application.Services.DepartmentService;
import com.example.campusMaster.application.dto.request.departments.RegisterDepartmentDTO;
import com.example.campusMaster.application.dto.request.departments.UpdateDepartmentDTO;
import com.example.campusMaster.application.dto.request.users.UpdateStatusRequest;
import com.example.campusMaster.application.dto.response.Pagination;
import com.example.campusMaster.application.dto.response.departments.DepartmentResponse;
import com.example.campusMaster.application.dto.response.departments.DepartmentWithModuleResponse;
import com.example.campusMaster.application.dto.response.departments.PageDepResp;
import com.example.campusMaster.domain.entity.Department;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Departments", description = "Gestion des départements")
public class DepartmentController {

        private final DepartmentService departmentService;

        @PostMapping
        public ResponseEntity<ApiSuccessResponse<DepartmentResponse>> createDepartment(
                        @Valid @RequestBody RegisterDepartmentDTO dto) {

                DepartmentResponse department = departmentService.createDepartment(dto);

                ApiSuccessResponse<DepartmentResponse> response = ApiSuccessResponse.<DepartmentResponse>builder()
                                .success(true)
                                .message("Department created successfully")
                                .data(department)
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<DepartmentWithModuleResponse> getDepartment(@PathVariable Long id) {
                return ResponseEntity.ok(departmentService.getDepartmentById(id));
        }

        @GetMapping("/{id}/stats")
        public ResponseEntity<DepartmentWithModuleResponse> getStats(@PathVariable Long id) {
                return ResponseEntity.ok(departmentService.getDepartmentById(id));
        }

        @GetMapping
        public ResponseEntity<PageDepResp<DepartmentResponse>> getAllDepartments(
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int limit,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortOrder) {

                Page<Department> departments = departmentService.getAllDepartments(
                                search, isActive, page, limit, sortBy, sortOrder);

                PageDepResp<DepartmentResponse> response = PageDepResp.<DepartmentResponse>builder()
                                .data(
                                                departments.getContent().stream()
                                                                .map(this::convertToResponse)
                                                                .toList())
                                .pagination(Pagination.builder()
                                                .page(page)
                                                .limit(limit)
                                                .total(departments.getTotalElements())
                                                .totalPages(departments.getTotalPages())
                                                .build())
                                .stats(departmentService.getDepartmentStats())
                                .build();

                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<DepartmentWithModuleResponse> updateDepartment(
                        @PathVariable Long id,
                        @RequestBody UpdateDepartmentDTO dto) {

                return ResponseEntity.ok(
                                departmentService.updateDepartment(id, dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
                departmentService.deleteDepartment(id);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}")
        public ResponseEntity<DepartmentWithModuleResponse> patchDepartment(
                        @PathVariable Long id,
                        @RequestBody UpdateStatusRequest request) {
                        DepartmentWithModuleResponse response = departmentService.patchDepartment(id, request.getIsActive());
                        return ResponseEntity.ok(response);
        }

        private DepartmentResponse convertToResponse(Department department) {
                return DepartmentResponse.builder()
                                .id(department.getId())
                                .name(department.getName())
                                .description(department.getDescription())
                                .code(department.getCode())
                                .isActive(department.getIsActive())
                                .createdAt(department.getCreatedAt())
                                .updatedAt(department.getUpdatedAt())

                                .build();
        }

}
