package com.druginteraction.system.service;

import com.druginteraction.system.dto.CheckInteractionRequest;
import com.druginteraction.system.dto.DuplicateTherapyResponse;
import com.druginteraction.system.dto.InteractionCheckResponse;
import com.druginteraction.system.dto.InteractionRequest;
import com.druginteraction.system.dto.InteractionResponse;
import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.entity.Interaction;
import com.druginteraction.system.entity.Severity;
import com.druginteraction.system.repository.InteractionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for interaction-related business logic.
 * Contains the main feature: checking drug interactions.
 */
@Service
@Transactional
public class InteractionService {

    /** Most severe first, so the riskiest pair is always at the top. */
    private static final Map<Severity, Integer> SEVERITY_RANK = new LinkedHashMap<>();

    static {
        SEVERITY_RANK.put(Severity.HIGH, 0);
        SEVERITY_RANK.put(Severity.MEDIUM, 1);
        SEVERITY_RANK.put(Severity.LOW, 2);
    }

    private static final Comparator<InteractionResponse> SEVERITY_ORDER =
            Comparator.comparingInt(response -> SEVERITY_RANK.getOrDefault(response.getSeverity(), 3));

    private final InteractionRepository interactionRepository;
    private final DrugService drugService;
    private final DuplicateTherapyDetector duplicateTherapyDetector = new DuplicateTherapyDetector();

    public InteractionService(InteractionRepository interactionRepository, DrugService drugService) {
        this.interactionRepository = interactionRepository;
        this.drugService = drugService;
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getAllInteractions() {
        return toSortedUniqueResponses(interactionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsBySeverity(Severity severity) {
        return toSortedUniqueResponses(interactionRepository.findBySeverity(severity));
    }

    /**
     * Filter the reference library by free text and optionally by severity.
     * Matching runs over both drug names, the class names, the summary and the
     * mechanism, so "NSAID" or "bleeding" both find relevant pairs.
     */
    @Transactional(readOnly = true)
    public List<InteractionResponse> searchInteractions(String query, Severity severity) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        return toSortedUniqueResponses(interactionRepository.findAll()).stream()
                .filter(response -> severity == null || response.getSeverity() == severity)
                .filter(response -> needle.isEmpty() || matches(response, needle))
                .collect(Collectors.toList());
    }

    private boolean matches(InteractionResponse response, String needle) {
        return contains(response.getDrug1(), needle)
                || contains(response.getDrug2(), needle)
                || contains(response.getDescription(), needle)
                || contains(response.getMechanism(), needle)
                || contains(response.getManagement(), needle);
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    /**
     * Add a new interaction.
     *
     * @throws IllegalArgumentException if drugs are missing or the pair exists
     */
    public InteractionResponse addInteraction(InteractionRequest request) {
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

        Optional<Interaction> existingInteraction = interactionRepository
                .findInteractionBetweenDrugs(drug1, drug2);
        if (existingInteraction.isPresent()) {
            throw new IllegalArgumentException("Interaction between " + drug1.getName() +
                    " and " + drug2.getName() + " already exists");
        }

        Interaction interaction = new Interaction(
                drug1,
                drug2,
                request.getSeverity(),
                request.getDescription().trim(),
                trimToNull(request.getMechanism()),
                trimToNull(request.getManagement()),
                trimToNull(request.getEvidence()));
        return InteractionResponse.fromEntity(interactionRepository.save(interaction));
    }

    /**
     * Check a regimen (MAIN FEATURE). Returns pairwise interactions, the names
     * that resolved, the names that did not, and any duplicated drug classes.
     */
    @Transactional(readOnly = true)
    public InteractionCheckResponse checkInteractions(CheckInteractionRequest request) {
        List<String> submittedNames = request.getDrugs() == null ? List.of() : request.getDrugs();

        // Trim, drop blanks and collapse case-insensitive duplicates.
        Map<String, String> uniqueNames = new LinkedHashMap<>();
        for (String submitted : submittedNames) {
            if (submitted == null) {
                continue;
            }
            String name = submitted.trim();
            if (!name.isEmpty()) {
                uniqueNames.putIfAbsent(name.toLowerCase(Locale.ROOT), name);
            }
        }

        if (uniqueNames.size() < 2) {
            return new InteractionCheckResponse(
                    List.of(), List.copyOf(uniqueNames.values()), List.of(), List.of());
        }

        List<Drug> drugs = drugService.getDrugsByNames(List.copyOf(uniqueNames.values()));

        Map<String, Drug> resolved = new LinkedHashMap<>();
        for (Drug drug : drugs) {
            resolved.putIfAbsent(drug.getName().toLowerCase(Locale.ROOT), drug);
        }

        List<String> checkedDrugs = resolved.values().stream()
                .map(Drug::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        List<String> unknownDrugs = uniqueNames.entrySet().stream()
                .filter(entry -> !resolved.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        List<DuplicateTherapyResponse> duplicateTherapies = findDuplicateTherapies(resolved.values());

        if (resolved.size() < 2) {
            return new InteractionCheckResponse(
                    List.of(), checkedDrugs, unknownDrugs, duplicateTherapies);
        }

        List<Long> drugIds = resolved.values().stream()
                .map(Drug::getId)
                .collect(Collectors.toList());

        List<Interaction> foundInteractions = interactionRepository.findInteractionsAmongDrugs(drugIds);

        return new InteractionCheckResponse(
                toSortedUniqueResponses(foundInteractions),
                checkedDrugs,
                unknownDrugs,
                duplicateTherapies);
    }

    /**
     * Report repeated therapy using the curated rules in
     * {@link DuplicateTherapyDetector}. Raw class equality is not enough: some
     * repeats are deliberate and others are errors.
     */
    private List<DuplicateTherapyResponse> findDuplicateTherapies(Iterable<Drug> drugs) {
        List<Drug> resolved = new ArrayList<>();
        drugs.forEach(resolved::add);
        return duplicateTherapyDetector.detect(resolved);
    }

    public boolean deleteInteraction(Long id) {
        if (interactionRepository.existsById(id)) {
            interactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public InteractionResponse getInteractionById(Long id) {
        return interactionRepository.findById(id)
                .map(InteractionResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Convert entities to responses, collapsing duplicate pairs and sorting
     * from HIGH to LOW severity.
     */
    private List<InteractionResponse> toSortedUniqueResponses(List<Interaction> interactions) {
        Map<String, InteractionResponse> byPair = new LinkedHashMap<>();

        for (Interaction interaction : interactions) {
            String name1 = interaction.getDrug1().getName();
            String name2 = interaction.getDrug2().getName();
            byPair.putIfAbsent(pairKey(name1, name2), InteractionResponse.fromEntity(interaction));
        }

        List<InteractionResponse> responses = new ArrayList<>(byPair.values());
        responses.sort(SEVERITY_ORDER.thenComparing(
                response -> response.getDrug1() + "|" + response.getDrug2(),
                String.CASE_INSENSITIVE_ORDER));
        return responses;
    }

    /** Direction-independent key so A+B and B+A are the same pair. */
    private String pairKey(String name1, String name2) {
        String first = name1.toLowerCase(Locale.ROOT);
        String second = name2.toLowerCase(Locale.ROOT);
        return first.compareTo(second) <= 0 ? first + "|" + second : second + "|" + first;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
