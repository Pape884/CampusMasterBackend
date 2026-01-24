package com.example.campusMaster.application.dto.response.departments;

import java.util.List;

import com.example.campusMaster.application.dto.response.modules.ModuleResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentWithModuleResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private List<ModuleResponse> modules;
}
