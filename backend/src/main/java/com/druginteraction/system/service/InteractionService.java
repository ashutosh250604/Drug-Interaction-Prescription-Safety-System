package com.druginteraction.system.service;

import com.druginteraction.system.dto.CheckInteractionRequest;
import com.druginteraction.system.dto.InteractionRequest;
import com.druginteraction.system.dto.InteractionResponse;
import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.entity.Interaction;
import com.druginteraction.system.entity.Severity;
import com.druginteraction.system.repository.InteractionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for interaction-related business logic.
 * Contains the main feature: checking drug interactions.
 */
@Service
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final DrugService drugService;

    public InteractionService(InteractionRepository interactionRepository, DrugService drugService) {
        this.interactionRepository = interactionRepository;
        this.drugService = drugService;
    }

    /**
     * Get all interactions.
     * 
     * @return List of all interactions
     */
    @Transactional(readOnly = true)
    public List<InteractionResponse> getAllInteractions() {
        return interactionRepository.findAll().stream()
                .map(InteractionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get interactions by severity level.
     * 
     * @param severity The severity level
     * @return List of interactions with the given severity
     */
    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsBySeverity(Severity severity) {
        return interactionRepository.findBySeverity(severity).stream()
                .map(InteractionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Add a new interaction.
     * 
     * @param request The interaction request
     * @return The created interaction response
     * @throws IllegalArgumentException if drugs not found or interaction already
     *                                  exists
     */
    public InteractionResponse addInteraction(InteractionRequest request) {
        // Find drugs by name
        Drug drug1 = drugService.getDrugByName(request.getDrug1Name());
        Drug drug2 = drugService.getDrugByName(request.getDrug2Name());

        if (drug1 == null) {
            throw new IllegalArgumentException("Drug not found: " + request.getDrug1Name());
        }
        if (drug2 == null) {
            throw new IllegalArgumentException("Drug not found: " + request.getDrug2Name());
        }
        if (drug1.getId().equals(drug2.getId())) {
            throw new IllegalArgumentException("Cannot create interaction between a drug and itself");
        }

        // Check if interaction already exists
        Optional<Interaction> existingInteraction = interactionRepository
                .findInteractionBetweenDrugs(drug1, drug2);
        if (existingInteraction.isPresent()) {
            throw new IllegalArgumentException("Interaction between " + request.getDrug1Name() +
                    " and " + request.getDrug2Name() + " already exists");
        }

        Interaction interaction = new Interaction(drug1, drug2, request.getSeverity(), request.getDescription());
        Interaction savedInteraction = interactionRepository.save(interaction);
        return InteractionResponse.fromEntity(savedInteraction);
    }

    /**
     * Check interactions for a list of drugs (MAIN FEATURE).
     * Compares all possible pairs and returns matching interactions.
     * 
     * @param request The check interaction request containing drug names
     * @return List of found interactions
     */
    @Transactional(readOnly = true)
    public List<InteractionResponse> checkInteractions(CheckInteractionRequest request) {
        List<String> drugNames = request.getDrugs();

        if (drugNames == null || drugNames.size() < 2) {
            return new ArrayList<>();
        }

        // Get all drugs and filter out any that don't exist
        List<Drug> drugs = drugService.getDrugsByNames(drugNames);

        if (drugs.size() < 2) {
            return new ArrayList<>();
        }

        // Extract drug IDs for bulk query
        List<Long> drugIds = drugs.stream()
                .map(Drug::getId)
                .collect(Collectors.toList());

        // Use bulk query to get all interactions among these drugs
        List<Interaction> foundInteractions = interactionRepository.findInteractionsAmongDrugs(drugIds);

        return foundInteractions.stream()
                .map(InteractionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Delete an interaction by ID.
     * 
     * @param id The interaction ID
     * @return true if deleted, false if not found
     */
    public boolean deleteInteraction(Long id) {
        if (interactionRepository.existsById(id)) {
            interactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get an interaction by ID.
     * 
     * @param id The interaction ID
     * @return The interaction response or null if not found
     */
    @Transactional(readOnly = true)
    public InteractionResponse getInteractionById(Long id) {
        return interactionRepository.findById(id)
                .map(InteractionResponse::fromEntity)
                .orElse(null);
    }
}
