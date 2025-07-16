package com.example.SmartLifeTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com/example/SmartLifeTracker/dto")
public class SmartLifeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLifeTrackerApplication.class, args);
	}

}
