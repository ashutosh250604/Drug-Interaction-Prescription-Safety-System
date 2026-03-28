package com.druginteraction.system.service;

import com.druginteraction.system.dto.DrugRequest;
import com.druginteraction.system.dto.DrugResponse;
import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.repository.DrugRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for drug-related business logic.
 */
@Service
@Transactional
public class DrugService {

    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    /**
     * Get all drugs.
     * 
     * @return List of all drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> getAllDrugs() {
        return drugRepository.findAll().stream()
                .map(DrugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a drug by ID.
     * 
     * @param id The drug ID
     * @return The drug response or null if not found
     */
    @Transactional(readOnly = true)
    public DrugResponse getDrugById(Long id) {
        return drugRepository.findById(id)
                .map(DrugResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Get a drug by name (case-insensitive).
     * 
     * @param name The drug name
     * @return The drug or null if not found
     */
    @Transactional(readOnly = true)
    public Drug getDrugByName(String name) {
        return drugRepository.findByNameIgnoreCase(name).orElse(null);
    }

    /**
     * Add a new drug.
     * 
     * @param request The drug request
     * @return The created drug response
     * @throws IllegalArgumentException if drug already exists
     */
    public DrugResponse addDrug(DrugRequest request) {
        // Check if drug already exists
        if (drugRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Drug with name '" + request.getName() + "' already exists");
        }

        Drug drug = new Drug(request.getName(), request.getDescription());
        Drug savedDrug = drugRepository.save(drug);
        return DrugResponse.fromEntity(savedDrug);
    }

    /**
     * Update an existing drug.
     * 
     * @param id      The drug ID
     * @param request The drug request
     * @return The updated drug response or null if not found
     */
    public DrugResponse updateDrug(Long id, DrugRequest request) {
        return drugRepository.findById(id)
                .map(drug -> {
                    // Check if the new name conflicts with another drug
                    if (!drug.getName().equalsIgnoreCase(request.getName()) &&
                            drugRepository.existsByNameIgnoreCase(request.getName())) {
                        throw new IllegalArgumentException("Drug with name '" + request.getName() + "' already exists");
                    }
                    drug.setName(request.getName());
                    drug.setDescription(request.getDescription());
                    return DrugResponse.fromEntity(drugRepository.save(drug));
                })
                .orElse(null);
    }

    /**
     * Delete a drug by ID.
     * 
     * @param id The drug ID
     * @return true if deleted, false if not found
     */
    public boolean deleteDrug(Long id) {
        if (drugRepository.existsById(id)) {
            drugRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search drugs by name.
     * 
     * @param name The search term
     * @return List of matching drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> searchDrugs(String name) {
        return drugRepository.findByNameContainingIgnoreCase(name).stream()
                .map(DrugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get drugs by a list of names.
     * 
     * @param names List of drug names
     * @return List of matching drugs
     */
    @Transactional(readOnly = true)
    public List<Drug> getDrugsByNames(List<String> names) {
        return names.stream()
                .map(this::getDrugByName)
                .filter(drug -> drug != null)
                .collect(Collectors.toList());
    }
}
