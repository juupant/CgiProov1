package com.flightbooking.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.flightbooking.repository")
@EntityScan(basePackages = "com.flightbooking.model")
public class FlightBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }
} 