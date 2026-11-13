package com.druginteraction.system.dto;

import com.druginteraction.system.entity.Severity;

import java.util.List;

/**
 * Two or more medications in the same regimen that share a pharmacological
 * class, for example two NSAIDs or two anticoagulants. Duplicate therapy is
 * flagged separately from pairwise interactions because the risk comes from
 * the summed effect rather than from a specific pair.
 *
 * <p>{@code drugClass} is the label shown to the user; for an overlap between
 * two different classes it names both, for example "ACE inhibitor + ARB".
 * {@code oftenIntentional} marks combinations that are frequently deliberate
 * (dual antiplatelet therapy, two calcium channel blockers) so the wording
 * asks for confirmation instead of telling the user to stop one.
 */
public class DuplicateTherapyResponse {

    private String drugClass;
    private List<String> drugs;
    private Severity severity;
    private String message;
    private boolean oftenIntentional;

    public DuplicateTherapyResponse() {
    }

    public DuplicateTherapyResponse(String drugClass, List<String> drugs) {
        this(drugClass, drugs, Severity.MEDIUM, null, false);
    }

    public DuplicateTherapyResponse(String drugClass, List<String> drugs, Severity severity,
                                    String message, boolean oftenIntentional) {
        this.drugClass = drugClass;
        this.drugs = drugs;
        this.severity = severity;
        this.message = message;
        this.oftenIntentional = oftenIntentional;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOftenIntentional() {
        return oftenIntentional;
    }

    public void setOftenIntentional(boolean oftenIntentional) {
        this.oftenIntentional = oftenIntentional;
    }
}
