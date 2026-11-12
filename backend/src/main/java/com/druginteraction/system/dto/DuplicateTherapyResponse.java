package com.druginteraction.system.dto;

import java.util.List;

/**
 * Two or more medications in the same regimen that share a pharmacological
 * class, for example two NSAIDs or two anticoagulants. Duplicate therapy is
 * flagged separately from pairwise interactions because the risk comes from
 * the summed effect rather than from a specific pair.
 */
public class DuplicateTherapyResponse {

    private String drugClass;
    private List<String> drugs;

    public DuplicateTherapyResponse() {
    }

    public DuplicateTherapyResponse(String drugClass, List<String> drugs) {
        this.drugClass = drugClass;
        this.drugs = drugs;
    }

    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    public List<String> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<String> drugs) {
        this.drugs = drugs;
    }
}
