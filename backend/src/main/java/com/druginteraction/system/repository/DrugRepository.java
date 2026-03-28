package com.druginteraction.system.repository;

import com.druginteraction.system.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Drug entity operations.
 * Provides CRUD operations and custom queries for drugs.
 */
@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    /**
     * Find a drug by its name (case-insensitive).
     * @param name The name of the drug
     * @return Optional containing the drug if found
     */
    Optional<Drug> findByNameIgnoreCase(String name);

    /**
     * Check if a drug exists with the given name (case-insensitive).
     * @param name The name to check
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all drugs whose names contain the given string (case-insensitive).
     * @param name The partial name to search
     * @return List of matching drugs
     */
    List<Drug> findByNameContainingIgnoreCase(String name);
}
