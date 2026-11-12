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

    @Size(max = 1000, message = "Mechanism must not exceed 1000 characters")
    private String mechanism;

    @Size(max = 1000, message = "Management must not exceed 1000 characters")
    private String management;

    @Size(max = 40, message = "Evidence must not exceed 40 characters")
    private String evidence;

    public InteractionRequest() {
    }

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

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getManagement() {
        return management;
    }

    public void setManagement(String management) {
        this.management = management;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}
