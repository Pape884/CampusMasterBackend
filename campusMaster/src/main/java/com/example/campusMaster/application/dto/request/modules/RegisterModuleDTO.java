package com.example.campusMaster.application.dto.request.modules;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterModuleDTO {

    @NotBlank(message = "Code obligatoire")
    String code;
    
    @NotBlank(message = "Nom obligatoire")
    String name;

    @NotBlank
    private String semestre;

    @NotNull(message = "Department ID obligatoire")
    Long departmentId;


}
