package com.druginteraction.system.service;

import com.druginteraction.system.dto.DuplicateTherapyResponse;
import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.entity.Severity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateTherapyDetectorTest {

    private final DuplicateTherapyDetector detector = new DuplicateTherapyDetector();

    private Drug drug(String name, String drugClass) {
        return new Drug(name, drugClass, null);
    }

    @Test
    void flagsTwoAnticoagulantsAsHigh() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Warfarin", "Anticoagulant"),
                drug("Apixaban", "Anticoagulant")));

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDrugClass()).isEqualTo("Anticoagulant");
        assertThat(found.get(0).getSeverity()).isEqualTo(Severity.HIGH);
        assertThat(found.get(0).getDrugs()).containsExactly("Warfarin", "Apixaban");
        assertThat(found.get(0).isOftenIntentional()).isFalse();
    }

    @Test
    void marksDualAntiplateletAsOftenIntentional() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Aspirin", "Antiplatelet"),
                drug("Clopidogrel", "Antiplatelet")));

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getSeverity()).isEqualTo(Severity.MEDIUM);
        assertThat(found.get(0).isOftenIntentional()).isTrue();
        assertThat(found.get(0).getMessage()).contains("often intentional");
    }

    @Test
    void flagsAceInhibitorAndArbAcrossClasses() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Lisinopril", "ACE inhibitor"),
                drug("Losartan", "ARB")));

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDrugClass()).isEqualTo("ACE inhibitor + ARB");
        assertThat(found.get(0).getSeverity()).isEqualTo(Severity.HIGH);
        assertThat(found.get(0).getDrugs()).containsExactly("Lisinopril", "Losartan");
    }

    @Test
    void doesNotFlagDifferentClassesWithoutACrossRule() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Warfarin", "Anticoagulant"),
                drug("Aspirin", "Antiplatelet"),
                drug("Metformin", "Biguanide")));

        assertThat(found).isEmpty();
    }

    @Test
    void ignoresClassesWithNoCuratedRule() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Cetirizine", "Antihistamine"),
                drug("Loratadine", "Antihistamine")));

        assertThat(found).isEmpty();
    }

    @Test
    void ignoresMissingAndBlankClasses() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Drug A", null),
                drug("Drug B", "  ")));

        assertThat(found).isEmpty();
    }

    @Test
    void sortsHighSeverityBeforeLow() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Amlodipine", "Calcium channel blocker"),
                drug("Verapamil", "Calcium channel blocker"),
                drug("Warfarin", "Anticoagulant"),
                drug("Apixaban", "Anticoagulant")));

        assertThat(found).extracting(DuplicateTherapyResponse::getSeverity)
                .containsExactly(Severity.HIGH, Severity.LOW);
    }

    @Test
    void matchesClassNamesCaseInsensitively() {
        List<DuplicateTherapyResponse> found = detector.detect(List.of(
                drug("Warfarin", "anticoagulant"),
                drug("Apixaban", "ANTICOAGULANT")));

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDrugs()).containsExactly("Warfarin", "Apixaban");
    }
}
