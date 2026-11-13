package com.druginteraction.system.entity;

/**
 * Enum representing the severity level of drug interactions.
 * Used to categorize and color-code interaction warnings.
 */
public enum Severity {
    /**
     * Low severity - Minor interaction, usually safe with monitoring
     */
    LOW,

    /**
     * Medium severity - Moderate interaction, caution advised
     */
    MEDIUM,

    /**
     * High severity - Severe interaction, avoid combination
     */
    HIGH,

    /**
     * Not rated - the source lists the pair but assigns no severity. Kept
     * separate from the graded levels so it is never read as "moderate".
     */
    UNKNOWN
}
