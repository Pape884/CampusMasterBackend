package com.example.campusMaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class CampusMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusMasterApplication.class, args);
	}

}
