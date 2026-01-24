package com.example.campusMaster.application.dto.request.departments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

import com.example.campusMaster.application.dto.request.modules.RegisterModuleDTO;

@Data
public class RegisterDepartmentDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    @NotEmpty
    private List<RegisterModuleDTO> modules;
}

