package com.druginteraction.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing an interaction between two drugs.
 * Contains information about the severity and description of the interaction.
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

    // Default constructor required by JPA
    public Interaction() {
    }

    // Parameterized constructor
    public Interaction(Drug drug1, Drug drug2, Severity severity, String description) {
        this.drug1 = drug1;
        this.drug2 = drug2;
        this.severity = severity;
        this.description = description;
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

    @Override
    public String toString() {
        return "Interaction{" +
                "id=" + id +
                ", drug1=" + (drug1 != null ? drug1.getName() : "null") +
                ", drug2=" + (drug2 != null ? drug2.getName() : "null") +
                ", severity=" + severity +
                ", description='" + description + '\'' +
                '}';
    }
}
