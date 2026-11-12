package com.druginteraction.system.dto;

import com.druginteraction.system.entity.Interaction;
import com.druginteraction.system.entity.Severity;

/**
 * DTO for interaction response data.
 */
public class InteractionResponse {

    private Long id;
    private String drug1;
    private String drug2;
    private Severity severity;
    private String description;
    private String mechanism;
    private String management;
    private String evidence;

    public InteractionResponse() {
    }

    public InteractionResponse(Long id, String drug1, String drug2, Severity severity,
                               String description, String mechanism, String management,
                               String evidence) {
        this.id = id;
        this.drug1 = drug1;
        this.drug2 = drug2;
        this.severity = severity;
        this.description = description;
        this.mechanism = mechanism;
        this.management = management;
        this.evidence = evidence;
    }

    public static InteractionResponse fromEntity(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(),
                interaction.getDrug1().getName(),
                interaction.getDrug2().getName(),
                interaction.getSeverity(),
                interaction.getDescription(),
                interaction.getMechanism(),
                interaction.getManagement(),
                interaction.getEvidence());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDrug1() {
        return drug1;
    }

    public void setDrug1(String drug1) {
        this.drug1 = drug1;
    }

    public String getDrug2() {
        return drug2;
    }

    public void setDrug2(String drug2) {
        this.drug2 = drug2;
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
