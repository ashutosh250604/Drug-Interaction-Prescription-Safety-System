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

    @Transactional(readOnly = true)
    public List<DrugResponse> getAllDrugs() {
        return drugRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(DrugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DrugResponse getDrugById(Long id) {
        return drugRepository.findById(id)
                .map(DrugResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Drug getDrugByName(String name) {
        return drugRepository.findByNameIgnoreCase(name).orElse(null);
    }

    /**
     * Add a new drug.
     *
     * @throws IllegalArgumentException if the name already exists
     */
    public DrugResponse addDrug(DrugRequest request) {
        if (drugRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Drug with name '" + request.getName() + "' already exists");
        }

        Drug drug = new Drug(
                request.getName().trim(),
                trimToNull(request.getDrugClass()),
                trimToNull(request.getDescription()));
        return DrugResponse.fromEntity(drugRepository.save(drug));
    }

    public DrugResponse updateDrug(Long id, DrugRequest request) {
        return drugRepository.findById(id)
                .map(drug -> {
                    if (!drug.getName().equalsIgnoreCase(request.getName()) &&
                            drugRepository.existsByNameIgnoreCase(request.getName())) {
                        throw new IllegalArgumentException("Drug with name '" + request.getName() + "' already exists");
                    }
                    drug.setName(request.getName().trim());
                    drug.setDrugClass(trimToNull(request.getDrugClass()));
                    drug.setDescription(trimToNull(request.getDescription()));
                    return DrugResponse.fromEntity(drugRepository.save(drug));
                })
                .orElse(null);
    }

    public boolean deleteDrug(Long id) {
        if (drugRepository.existsById(id)) {
            drugRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<DrugResponse> searchDrugs(String name) {
        return drugRepository.findByNameContainingIgnoreCase(name).stream()
                .map(DrugResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Drug> getDrugsByNames(List<String> names) {
        return names.stream()
                .map(this::getDrugByName)
                .filter(drug -> drug != null)
                .collect(Collectors.toList());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
