package com.openclassrooms.mddapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MddApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MddApiApplication.class, args);
	}

}
