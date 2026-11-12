package com.druginteraction.system.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO returned by the interaction check endpoint.
 *
 * <ul>
 *     <li>{@code interactions} - known interactions, most severe first</li>
 *     <li>{@code checkedDrugs} - names that were found in the database</li>
 *     <li>{@code unknownDrugs} - names that could not be resolved</li>
 *     <li>{@code duplicateTherapies} - classes repeated in the same regimen</li>
 * </ul>
 *
 * Reporting {@code unknownDrugs} is a safety requirement: an unrecognised name
 * (usually a typo) must never be silently dropped, because that would produce a
 * reassuring "no interactions" answer for a pair that was never checked.
 */
public class InteractionCheckResponse {

    private List<InteractionResponse> interactions = new ArrayList<>();
    private List<String> checkedDrugs = new ArrayList<>();
    private List<String> unknownDrugs = new ArrayList<>();
    private List<DuplicateTherapyResponse> duplicateTherapies = new ArrayList<>();

    public InteractionCheckResponse() {
    }

    public InteractionCheckResponse(List<InteractionResponse> interactions,
                                    List<String> checkedDrugs,
                                    List<String> unknownDrugs,
                                    List<DuplicateTherapyResponse> duplicateTherapies) {
        this.interactions = interactions;
        this.checkedDrugs = checkedDrugs;
        this.unknownDrugs = unknownDrugs;
        this.duplicateTherapies = duplicateTherapies;
    }

    public List<InteractionResponse> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<InteractionResponse> interactions) {
        this.interactions = interactions;
    }

    public List<String> getCheckedDrugs() {
        return checkedDrugs;
    }

    public void setCheckedDrugs(List<String> checkedDrugs) {
        this.checkedDrugs = checkedDrugs;
    }

    public List<String> getUnknownDrugs() {
        return unknownDrugs;
    }

    public void setUnknownDrugs(List<String> unknownDrugs) {
        this.unknownDrugs = unknownDrugs;
    }

    public List<DuplicateTherapyResponse> getDuplicateTherapies() {
        return duplicateTherapies;
    }

    public void setDuplicateTherapies(List<DuplicateTherapyResponse> duplicateTherapies) {
        this.duplicateTherapies = duplicateTherapies;
    }
}
