package com.example.secure_notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.secure_notes"})
public class SecureNotesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SecureNotesApplication.class, args);
	}

}
