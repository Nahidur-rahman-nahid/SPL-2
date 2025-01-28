package com.TimeWise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@Async
@SpringBootApplication
public class TimeWiseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeWiseBackendApplication.class, args);
	}
}
