package com.druginteraction.system.dto;

import com.druginteraction.system.entity.Drug;

/**
 * DTO for drug response data.
 */
public class DrugResponse {

    private Long id;
    private String name;
    private String drugClass;
    private String description;

    public DrugResponse() {
    }

    public DrugResponse(Long id, String name, String drugClass, String description) {
        this.id = id;
        this.name = name;
        this.drugClass = drugClass;
        this.description = description;
    }

    public static DrugResponse fromEntity(Drug drug) {
        return new DrugResponse(
                drug.getId(),
                drug.getName(),
                drug.getDrugClass(),
                drug.getDescription());
    }

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
