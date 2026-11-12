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

    @Size(max = 80, message = "Drug class must not exceed 80 characters")
    private String drugClass;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    public DrugRequest() {
    }

    public DrugRequest(String name, String drugClass, String description) {
        this.name = name;
        this.drugClass = drugClass;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
