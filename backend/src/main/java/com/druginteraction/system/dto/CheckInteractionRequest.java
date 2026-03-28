package com.druginteraction.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for checking drug interactions request.
 * Contains a list of drug names to check for interactions.
 */
public class CheckInteractionRequest {

    @NotEmpty(message = "At least one drug is required")
    @Size(min = 2, message = "At least two drugs are required to check interactions")
    private List<String> drugs;

    // Default constructor
    public CheckInteractionRequest() {
    }

    // Parameterized constructor
    public CheckInteractionRequest(List<String> drugs) {
        this.drugs = drugs;
    }

    // Getter and Setter
    public List<String> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<String> drugs) {
        this.drugs = drugs;
    }
}
