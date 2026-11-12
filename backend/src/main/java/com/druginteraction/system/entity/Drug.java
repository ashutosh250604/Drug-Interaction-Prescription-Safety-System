package com.druginteraction.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity representing a drug in the system.
 * {@code drugClass} groups drugs that share a mechanism (for example NSAID or
 * ACE inhibitor) and powers duplicate-therapy warnings.
 */
@Entity
@Table(name = "drug")
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Drug name is required")
    @Size(min = 2, max = 255, message = "Drug name must be between 2 and 255 characters")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Size(max = 80, message = "Drug class must not exceed 80 characters")
    @Column(name = "drug_class", length = 80)
    private String drugClass;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    // Default constructor required by JPA
    public Drug() {
    }

    public Drug(String name, String drugClass, String description) {
        this.name = name;
        this.drugClass = drugClass;
        this.description = description;
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

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", drugClass='" + drugClass + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
