package com.example.campusMaster.application.dto.request.modules;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterModuleDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotBlank
    private String semestre;

}
