package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

public record ApiResponse <T>(Boolean success,
    String message,
    T data,
    LocalDateTime timestamp
) {
    /**
     * Créer une réponse de succès avec données et message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }
    
    /**
     * Créer une réponse de succès avec données uniquement
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Opération réussie");
    }
    
    /**
     * Créer une réponse d'erreur
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }

}
