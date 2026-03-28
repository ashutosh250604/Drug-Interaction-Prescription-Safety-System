package com.druginteraction.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Drug Interaction & Prescription Safety System.
 * This application provides REST APIs for managing drugs and checking drug interactions.
 */
@SpringBootApplication
public class DrugInteractionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugInteractionSystemApplication.class, args);
    }
}
