package com.druginteraction.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing an interaction between two drugs.
 * Besides the severity label, each record can explain the pharmacology
 * (mechanism), the recommended action (management) and how solid the evidence
 * is.
 */
@Entity
@Table(name = "interaction")
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Drug 1 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug1_id", nullable = false)
    private Drug drug1;

    @NotNull(message = "Drug 2 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug2_id", nullable = false)
    private Drug drug2;

    @NotNull(message = "Severity is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    private Severity severity;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 1000, message = "Mechanism must not exceed 1000 characters")
    @Column(name = "mechanism", length = 1000)
    private String mechanism;

    @Size(max = 1000, message = "Management must not exceed 1000 characters")
    @Column(name = "management", length = 1000)
    private String management;

    @Size(max = 40, message = "Evidence must not exceed 40 characters")
    @Column(name = "evidence", length = 40)
    private String evidence;

    /**
     * Where the row came from: CURATED for the hand-written set, DDINTER for
     * imported data. Curated rows are never overwritten by an import.
     */
    @Size(max = 20, message = "Source must not exceed 20 characters")
    @Column(name = "source", length = 20)
    private String source = "CURATED";

    // Default constructor required by JPA
    public Interaction() {
    }

    public Interaction(Drug drug1, Drug drug2, Severity severity, String description,
                       String mechanism, String management, String evidence) {
        this.drug1 = drug1;
        this.drug2 = drug2;
        this.severity = severity;
        this.description = description;
        this.mechanism = mechanism;
        this.management = management;
        this.evidence = evidence;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Drug getDrug1() {
        return drug1;
    }

    public void setDrug1(Drug drug1) {
        this.drug1 = drug1;
    }

    public Drug getDrug2() {
        return drug2;
    }

    public void setDrug2(Drug drug2) {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "id=" + id +
                ", drug1=" + (drug1 != null ? drug1.getName() : "null") +
                ", drug2=" + (drug2 != null ? drug2.getName() : "null") +
                ", severity=" + severity +
                '}';
    }
}
