package com.ahmed.AhmedMohmoud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "getAuditAware")
public class StackerRatApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackerRatApplication.class, args);
	}

}
