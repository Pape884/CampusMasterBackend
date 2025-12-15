package com.example.campusMaster.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.campusMaster.domain.repository.UserRepository;

@RestController
public class TestController {
    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test-db")
    public String testDatabase() {
        return "Nombre d'utilisateurs : " + userRepository.count();
    }
}