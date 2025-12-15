package com.example.campusMaster.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.campusMaster.domain.entity.User;

public interface UserRepository extends JpaRepository <User, Long> {

}
