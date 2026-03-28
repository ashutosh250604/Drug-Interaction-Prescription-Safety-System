package com.druginteraction.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for drug creation/update requests.
 */
public class DrugRequest {

    @NotBlank(message = "Drug name is required")
    @Size(min = 2, max = 255, message = "Drug name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    // Default constructor
    public DrugRequest() {
    }

    // Parameterized constructor
    public DrugRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
