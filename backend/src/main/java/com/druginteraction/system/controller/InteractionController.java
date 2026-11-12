package com.druginteraction.system.controller;

import com.druginteraction.system.dto.CheckInteractionRequest;
import com.druginteraction.system.dto.InteractionCheckResponse;
import com.druginteraction.system.dto.InteractionRequest;
import com.druginteraction.system.dto.InteractionResponse;
import com.druginteraction.system.entity.Severity;
import com.druginteraction.system.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for interaction-related endpoints.
 * Includes the main feature: /api/check for checking drug interactions.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /**
     * Get all interactions, most severe first.
     * GET /api/interactions
     */
    @GetMapping("/interactions")
    public ResponseEntity<List<InteractionResponse>> getAllInteractions() {
        return ResponseEntity.ok(interactionService.getAllInteractions());
    }

    /**
     * Get interactions by severity.
     * GET /api/interactions/by-severity?severity=HIGH
     */
    @GetMapping("/interactions/by-severity")
    public ResponseEntity<List<InteractionResponse>> getInteractionsBySeverity(
            @RequestParam Severity severity) {
        return ResponseEntity.ok(interactionService.getInteractionsBySeverity(severity));
    }

    /**
     * Search the reference library.
     * GET /api/interactions/filter?q=bleeding&severity=HIGH
     */
    @GetMapping("/interactions/filter")
    public ResponseEntity<List<InteractionResponse>> filterInteractions(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Severity severity) {
        return ResponseEntity.ok(interactionService.searchInteractions(q, severity));
    }

    /**
     * Get an interaction by ID.
     * GET /api/interactions/{id}
     */
    @GetMapping("/interactions/{id}")
    public ResponseEntity<InteractionResponse> getInteractionById(@PathVariable Long id) {
        InteractionResponse interaction = interactionService.getInteractionById(id);
        if (interaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(interaction);
    }

    /**
     * Add a new interaction.
     * POST /api/interactions
     */
    @PostMapping("/interactions")
    public ResponseEntity<?> addInteraction(@Valid @RequestBody InteractionRequest request) {
        try {
            InteractionResponse interaction = interactionService.addInteraction(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(interaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete an interaction.
     * DELETE /api/interactions/{id}
     */
    @DeleteMapping("/interactions/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        if (interactionService.deleteInteraction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Check drug interactions (MAIN FEATURE).
     * POST /api/check
     *
     * Returns pairwise interactions sorted HIGH to LOW, the resolved and
     * unresolved names, and any classes that appear twice in the regimen.
     */
    @PostMapping("/check")
    public ResponseEntity<InteractionCheckResponse> checkInteractions(
            @Valid @RequestBody CheckInteractionRequest request) {
        return ResponseEntity.ok(interactionService.checkInteractions(request));
    }
}
