package com.druginteraction.system.controller;

import com.druginteraction.system.dto.DrugRequest;
import com.druginteraction.system.dto.DrugResponse;
import com.druginteraction.system.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for drug-related endpoints.
 */
@RestController
@RequestMapping("/api/drugs")
@CrossOrigin(origins = "*")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    /**
     * Get all drugs.
     * GET /api/drugs
     */
    @GetMapping
    public ResponseEntity<List<DrugResponse>> getAllDrugs() {
        List<DrugResponse> drugs = drugService.getAllDrugs();
        return ResponseEntity.ok(drugs);
    }

    /**
     * Get a drug by ID.
     * GET /api/drugs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DrugResponse> getDrugById(@PathVariable Long id) {
        DrugResponse drug = drugService.getDrugById(id);
        if (drug == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(drug);
    }

    /**
     * Add a new drug.
     * POST /api/drugs
     */
    @PostMapping
    public ResponseEntity<?> addDrug(@Valid @RequestBody DrugRequest request) {
        try {
            DrugResponse drug = drugService.addDrug(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(drug);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update an existing drug.
     * PUT /api/drugs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDrug(@PathVariable Long id, @Valid @RequestBody DrugRequest request) {
        try {
            DrugResponse drug = drugService.updateDrug(id, request);
            if (drug == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(drug);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a drug.
     * DELETE /api/drugs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Long id) {
        if (drugService.deleteDrug(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Search drugs by name.
     * GET /api/drugs/search?name=keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<DrugResponse>> searchDrugs(@RequestParam String name) {
        List<DrugResponse> drugs = drugService.searchDrugs(name);
        return ResponseEntity.ok(drugs);
    }
}
