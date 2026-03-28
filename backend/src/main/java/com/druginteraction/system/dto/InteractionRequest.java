package com.druginteraction.system.dto;

import com.druginteraction.system.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for interaction creation requests.
 */
public class InteractionRequest {

    @NotBlank(message = "Drug 1 name is required")
    private String drug1Name;

    @NotBlank(message = "Drug 2 name is required")
    private String drug2Name;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    // Default constructor
    public InteractionRequest() {
    }

    // Parameterized constructor
    public InteractionRequest(String drug1Name, String drug2Name, Severity severity, String description) {
        this.drug1Name = drug1Name;
        this.drug2Name = drug2Name;
        this.severity = severity;
        this.description = description;
    }

    // Getters and Setters
    public String getDrug1Name() {
        return drug1Name;
    }

    public void setDrug1Name(String drug1Name) {
        this.drug1Name = drug1Name;
    }

    public String getDrug2Name() {
        return drug2Name;
    }

    public void setDrug2Name(String drug2Name) {
        this.drug2Name = drug2Name;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
