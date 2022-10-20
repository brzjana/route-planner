package com.inmods.routeplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

// Spring Annotation
@SpringBootApplication
public class RoutePlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutePlannerApplication.class, args);

		/*
		SpringApplication app = new SpringApplication(RoutePlannerApplication.class);
		app.setDefaultProperties(Collections
				.singletonMap("server.port", "8083"));
		app.run(args);

		 */
	}

}
