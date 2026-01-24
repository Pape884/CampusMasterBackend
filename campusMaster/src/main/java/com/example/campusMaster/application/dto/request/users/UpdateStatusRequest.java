package com.example.campusMaster.application.dto.request.users;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateStatusRequest {
    @NotNull(message = "Le statut est obligatoire")
    private Boolean isActive;

}
