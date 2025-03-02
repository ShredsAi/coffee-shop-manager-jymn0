package ai.shreds.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class InfrastructureRepositoryException extends RuntimeException {

    private final String errorCode;
    private final String entityType;
    private final String operation;

    public InfrastructureRepositoryException(String message) {
        this(message, null, null, null, null);
    }

    public InfrastructureRepositoryException(String message, Throwable cause) {
        this(message, cause, null, null, null);
    }

    public InfrastructureRepositoryException(String message, String errorCode, String entityType, String operation) {
        this(message, null, errorCode, entityType, operation);
    }

    public InfrastructureRepositoryException(String message, Throwable cause, String errorCode, 
            String entityType, String operation) {
        super(message, cause);
        this.errorCode = errorCode != null ? errorCode : "REPOSITORY_ERROR";
        this.entityType = entityType;
        this.operation = operation;
    }

    public static InfrastructureRepositoryException entityNotFound(String entityType, String identifier) {
        return new InfrastructureRepositoryException(
            String.format("%s with identifier %s not found", entityType, identifier),
            "ENTITY_NOT_FOUND",
            entityType,
            "READ"
        );
    }

    public static InfrastructureRepositoryException persistenceError(String entityType, String operation, Throwable cause) {
        return new InfrastructureRepositoryException(
            String.format("Error performing %s operation on %s", operation, entityType),
            cause,
            "PERSISTENCE_ERROR",
            entityType,
            operation
        );
    }

    public static InfrastructureRepositoryException validationError(String entityType, String message) {
        return new InfrastructureRepositoryException(
            message,
            "VALIDATION_ERROR",
            entityType,
            "VALIDATE"
        );
    }

    public static InfrastructureRepositoryException concurrencyError(String entityType, String identifier) {
        return new InfrastructureRepositoryException(
            String.format("Concurrent modification detected for %s with identifier %s", entityType, identifier),
            "CONCURRENCY_ERROR",
            entityType,
            "UPDATE"
        );
    }

    public static InfrastructureRepositoryException connectionError(String operation, Throwable cause) {
        return new InfrastructureRepositoryException(
            String.format("Database connection error during %s operation", operation),
            cause,
            "CONNECTION_ERROR",
            null,
            operation
        );
    }
}
