package com.druginteraction.system.service;

import com.druginteraction.system.dto.DuplicateTherapyResponse;
import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.entity.Severity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Detects repeated therapy within one regimen.
 *
 * <p>Sharing a {@code drug_class} string is not enough on its own: two
 * antiplatelets are usually deliberate dual antiplatelet therapy and two
 * calcium channel blockers are sometimes a planned combination, while two
 * anticoagulants are almost always a prescribing error. Each rule therefore
 * carries its own severity and explanation, and only classes that appear here
 * are reported. A second list covers risky overlaps between two different
 * classes, such as an ACE inhibitor with an ARB.
 *
 * <p>This class has no Spring dependencies so the rules can be unit tested
 * without a database.
 */
public final class DuplicateTherapyDetector {

    private record GroupRule(String label, Severity severity, String message,
                             boolean oftenIntentional, List<String> classes) {
    }

    private record CrossRule(String label, Severity severity, String message,
                             String firstClass, String secondClass) {
    }

    /** Rules for two or more drugs that share one class. */
    private static final List<GroupRule> GROUP_RULES = List.of(
            new GroupRule("Anticoagulant", Severity.HIGH,
                    "Two anticoagulants together: major bleeding risk without extra benefit. "
                            + "This is usually a switching error, so confirm which one is intended.",
                    false, List.of("anticoagulant")),
            new GroupRule("NSAID", Severity.HIGH,
                    "Two NSAIDs together: additive gastrointestinal and renal toxicity "
                            + "without extra pain relief.",
                    false, List.of("nsaid")),
            new GroupRule("Opioid analgesic", Severity.HIGH,
                    "Two opioids together: additive respiratory depression and sedation.",
                    false, List.of("opioid analgesic")),
            new GroupRule("SSRI", Severity.MEDIUM,
                    "Two SSRIs together: serotonin syndrome and hyponatraemia risk "
                            + "without extra benefit.",
                    false, List.of("ssri")),
            new GroupRule("Statin", Severity.MEDIUM,
                    "Two statins together: myopathy and rhabdomyolysis risk "
                            + "without extra lipid lowering.",
                    false, List.of("statin")),
            new GroupRule("Antiplatelet", Severity.MEDIUM,
                    "Two antiplatelets together: often intentional as dual antiplatelet therapy. "
                            + "Confirm the indication and the planned duration.",
                    true, List.of("antiplatelet")),
            new GroupRule("Calcium channel blocker", Severity.LOW,
                    "Two calcium channel blockers together: sometimes intentional for rate "
                            + "and blood pressure control. Confirm the combination.",
                    true, List.of("calcium channel blocker"))
    );

    /** Rules for risky overlaps between two different classes. */
    private static final List<CrossRule> CROSS_RULES = List.of(
            new CrossRule("ACE inhibitor + ARB", Severity.HIGH,
                    "Dual renin-angiotensin blockade: hyperkalaemia and acute kidney injury "
                            + "without extra benefit.",
                    "ace inhibitor", "arb"),
            new CrossRule("Loop + thiazide diuretic", Severity.MEDIUM,
                    "Two diuretic classes together: additive volume and electrolyte loss. "
                            + "Often intentional in resistant oedema, but monitor electrolytes "
                            + "and renal function.",
                    "loop diuretic", "thiazide diuretic")
    );

    private static final Map<Severity, Integer> SEVERITY_RANK = Map.of(
            Severity.HIGH, 0,
            Severity.MEDIUM, 1,
            Severity.LOW, 2);

    public List<DuplicateTherapyResponse> detect(Collection<Drug> drugs) {
        Map<String, List<String>> namesByClass = new LinkedHashMap<>();
        for (Drug drug : drugs) {
            String key = normalise(drug.getDrugClass());
            if (key.isEmpty()) {
                continue;
            }
            namesByClass.computeIfAbsent(key, ignored -> new ArrayList<>()).add(drug.getName());
        }

        List<DuplicateTherapyResponse> found = new ArrayList<>();

        for (GroupRule rule : GROUP_RULES) {
            List<String> names = new ArrayList<>();
            for (String drugClass : rule.classes()) {
                names.addAll(namesByClass.getOrDefault(normalise(drugClass), List.of()));
            }
            if (names.size() > 1) {
                found.add(new DuplicateTherapyResponse(
                        rule.label(), names, rule.severity(), rule.message(), rule.oftenIntentional()));
            }
        }

        for (CrossRule rule : CROSS_RULES) {
            List<String> first = namesByClass.getOrDefault(normalise(rule.firstClass()), List.of());
            List<String> second = namesByClass.getOrDefault(normalise(rule.secondClass()), List.of());
            if (first.isEmpty() || second.isEmpty()) {
                continue;
            }
            List<String> names = new ArrayList<>(first);
            names.addAll(second);
            found.add(new DuplicateTherapyResponse(
                    rule.label(), names, rule.severity(), rule.message(), false));
        }

        found.sort(Comparator
                .comparingInt((DuplicateTherapyResponse response) ->
                        SEVERITY_RANK.getOrDefault(response.getSeverity(), 3))
                .thenComparing(DuplicateTherapyResponse::getDrugClass, String.CASE_INSENSITIVE_ORDER));
        return found;
    }

    /**
     * Lowercase and collapse whitespace so "ACE  inhibitor " and "ace inhibitor"
     * are treated as the same class.
     */
    private String normalise(String drugClass) {
        return drugClass == null
                ? ""
                : drugClass.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
