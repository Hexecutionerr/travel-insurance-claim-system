package com.academic.travelinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Travel Insurance Claim System.
 * Spring Boot auto-configures Web, JPA, and H2 database.
 */
@SpringBootApplication
public class TravelInsuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelInsuranceApplication.class, args);
    }
}
