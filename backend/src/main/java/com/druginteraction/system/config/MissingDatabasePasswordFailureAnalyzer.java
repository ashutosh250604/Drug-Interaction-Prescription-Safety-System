package com.druginteraction.system.config;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Turns the missing-password failure into a short "APPLICATION FAILED TO START"
 * report with the exact commands needed to fix it, instead of a stack trace.
 */
public class MissingDatabasePasswordFailureAnalyzer
        extends AbstractFailureAnalyzer<DatabasePasswordEnvironmentPostProcessor.MissingDatabasePasswordException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure,
                                      DatabasePasswordEnvironmentPostProcessor.MissingDatabasePasswordException cause) {
        String action = String.join(System.lineSeparator(),
                "Set the DB_PASSWORD environment variable in the same shell that starts the backend:",
                "",
                "  PowerShell:  $env:DB_PASSWORD = \"your-mysql-password\"; mvn spring-boot:run",
                "  Git Bash:    export DB_PASSWORD=your-mysql-password && mvn spring-boot:run",
                "  macOS/Linux: export DB_PASSWORD=your-mysql-password && mvn spring-boot:run",
                "",
                "In IntelliJ IDEA: Run > Edit Configurations > Environment variables > DB_PASSWORD=your-mysql-password",
                "",
                "The password is read only from the environment; application.properties no longer stores it.");

        return new FailureAnalysis(
                "The database password is not set (" + DatabasePasswordEnvironmentPostProcessor.PASSWORD_VARIABLE
                        + " is empty), so the application cannot connect to MySQL.",
                action,
                cause);
    }
}
