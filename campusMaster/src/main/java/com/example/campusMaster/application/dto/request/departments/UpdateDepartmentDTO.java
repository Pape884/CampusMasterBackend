package com.example.campusMaster.application.dto.request.departments;

import java.util.List;

import com.example.campusMaster.application.dto.request.modules.UpdateModuleDTO;

import lombok.Data;

@Data
public class UpdateDepartmentDTO {
    private String name;
    private String code;
    private String description;
    private List<UpdateModuleDTO> modules;
}
        