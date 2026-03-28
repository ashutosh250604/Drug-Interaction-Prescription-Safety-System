package com.druginteraction.system.repository;

import com.druginteraction.system.entity.Drug;
import com.druginteraction.system.entity.Interaction;
import com.druginteraction.system.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Interaction entity operations.
 * Provides CRUD operations and custom queries for drug interactions.
 */
@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    /**
     * Find all interactions involving a specific drug.
     * 
     * @param drug The drug to find interactions for
     * @return List of interactions
     */
    List<Interaction> findByDrug1OrDrug2(Drug drug1, Drug drug2);

    /**
     * Find interactions between two specific drugs (in either direction).
     * 
     * @param drug1 The first drug
     * @param drug2 The second drug
     * @return Optional containing the interaction if found
     */
    @Query("SELECT i FROM Interaction i WHERE " +
            "(i.drug1 = :drug1 AND i.drug2 = :drug2) OR " +
            "(i.drug1 = :drug2 AND i.drug2 = :drug1)")
    Optional<Interaction> findInteractionBetweenDrugs(
            @Param("drug1") Drug drug1,
            @Param("drug2") Drug drug2);

    /**
     * Find all interactions by severity level.
     * 
     * @param severity The severity level to filter by
     * @return List of interactions with the given severity
     */
    List<Interaction> findBySeverity(Severity severity);

    /**
     * Check if an interaction exists between two drugs.
     * 
     * @param drug1 The first drug
     * @param drug2 The second drug
     * @return true if interaction exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Interaction i WHERE " +
            "(i.drug1 = :drug1 AND i.drug2 = :drug2) OR " +
            "(i.drug1 = :drug2 AND i.drug2 = :drug1)")
    boolean existsInteractionBetweenDrugs(
            @Param("drug1") Drug drug1,
            @Param("drug2") Drug drug2);

    /**
     * Find all interactions for a list of drugs (comparing all pairs).
     * 
     * @param drugIds List of drug IDs to check
     * @return List of interactions found
     */
    @Query("SELECT i FROM Interaction i WHERE " +
            "i.drug1.id IN :drugIds AND i.drug2.id IN :drugIds")
    List<Interaction> findInteractionsAmongDrugs(@Param("drugIds") List<Long> drugIds);
}
