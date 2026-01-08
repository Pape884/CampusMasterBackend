package com.example.campusMaster.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint - accessible à tous";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Protected endpoint - besoin d'authentification";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Admin endpoint - besoin du rôle ADMIN";
    }
}
