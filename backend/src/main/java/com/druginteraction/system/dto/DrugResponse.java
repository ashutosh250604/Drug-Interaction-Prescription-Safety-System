package com.druginteraction.system.dto;

import com.druginteraction.system.entity.Drug;

/**
 * DTO for drug response data.
 */
public class DrugResponse {

    private Long id;
    private String name;
    private String description;

    // Default constructor
    public DrugResponse() {
    }

    // Parameterized constructor
    public DrugResponse(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Static factory method to create from entity
    public static DrugResponse fromEntity(Drug drug) {
        return new DrugResponse(
                drug.getId(),
                drug.getName(),
                drug.getDescription());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
