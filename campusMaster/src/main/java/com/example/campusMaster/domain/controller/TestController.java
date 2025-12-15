package com.example.campusMaster.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test-db")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping
    public String test() {
        long count = userRepository.count();
        return "Connexion OK - Nombre d'utilisateurs : " + count;
    }
}
