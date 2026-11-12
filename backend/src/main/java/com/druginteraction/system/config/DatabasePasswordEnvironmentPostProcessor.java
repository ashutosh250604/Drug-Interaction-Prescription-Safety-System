package com.druginteraction.system.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Stops the application at startup when the database password is missing.
 *
 * <p>application.properties declares {@code spring.datasource.password=${DB_PASSWORD}}
 * with no default. Spring binding resolves an absent variable to an empty string, so
 * without this check the first symptom is a confusing MySQL "Access denied" error
 * during Hibernate initialisation. Running as an environment post-processor means the
 * failure happens before any bean, and therefore before any connection attempt.
 */
public class DatabasePasswordEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PASSWORD_PROPERTY = "spring.datasource.password";
    static final String PASSWORD_VARIABLE = "DB_PASSWORD";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (passwordIsMissing(environment)) {
            throw new MissingDatabasePasswordException();
        }
    }

    private boolean passwordIsMissing(ConfigurableEnvironment environment) {
        String password;
        try {
            password = environment.getProperty(PASSWORD_PROPERTY);
        } catch (IllegalArgumentException unresolvedPlaceholder) {
            return true;
        }
        return password == null || password.isBlank();
    }

    @Override
    public int getOrder() {
        // After the application.properties property source has been loaded.
        return Ordered.LOWEST_PRECEDENCE;
    }

    /** Raised when neither the environment variable nor an override supplies a password. */
    public static class MissingDatabasePasswordException extends IllegalStateException {

        public MissingDatabasePasswordException() {
            super("Missing required environment variable " + PASSWORD_VARIABLE);
        }
    }
}
