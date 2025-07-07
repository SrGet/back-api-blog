package com.api.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProjectApiBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApiBlogApplication.class, args);
	}

}
