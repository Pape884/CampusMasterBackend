package com.example.campusMaster.application.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
