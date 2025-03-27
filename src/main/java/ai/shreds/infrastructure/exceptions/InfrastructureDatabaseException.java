package ai.shreds.infrastructure.exceptions;

import org.hibernate.StaleObjectStateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataAccessException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.sql.SQLException;

public class InfrastructureDatabaseException extends RuntimeException {

    private final String errorCode;

    public InfrastructureDatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InfrastructureDatabaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static InfrastructureDatabaseException fromJpaException(Exception e) {
        if (e instanceof OptimisticLockingFailureException || e instanceof StaleObjectStateException) {
            return new InfrastructureDatabaseException(
                "Concurrent modification detected. Please retry the operation.",
                "CONCURRENT_MODIFICATION",
                e
            );
        }

        if (e instanceof EntityNotFoundException) {
            return new InfrastructureDatabaseException(
                "Requested entity not found in database.",
                "ENTITY_NOT_FOUND",
                e
            );
        }

        if (e instanceof DataIntegrityViolationException) {
            return new InfrastructureDatabaseException(
                "Data integrity violation. Please check your input data.",
                "DATA_INTEGRITY_VIOLATION",
                e
            );
        }

        if (e instanceof PersistenceException) {
            return new InfrastructureDatabaseException(
                "Database persistence error occurred.",
                "PERSISTENCE_ERROR",
                e
            );
        }

        if (e instanceof SQLException) {
            return new InfrastructureDatabaseException(
                "SQL error occurred while accessing the database.",
                "SQL_ERROR",
                e
            );
        }

        if (e instanceof DataAccessException) {
            return new InfrastructureDatabaseException(
                "Error accessing the database.",
                "DATA_ACCESS_ERROR",
                e
            );
        }

        // Generic database exception for unhandled cases
        return new InfrastructureDatabaseException(
            "Unexpected database error occurred: " + e.getMessage(),
            "UNKNOWN_ERROR",
            e
        );
    }

    /**
     * Creates a specific exception for transaction-related errors
     */
    public static InfrastructureDatabaseException transactionError(String message, Throwable cause) {
        return new InfrastructureDatabaseException(
            "Transaction error: " + message,
            "TRANSACTION_ERROR",
            cause
        );
    }

    /**
     * Creates a specific exception for connection-related errors
     */
    public static InfrastructureDatabaseException connectionError(String message, Throwable cause) {
        return new InfrastructureDatabaseException(
            "Database connection error: " + message,
            "CONNECTION_ERROR",
            cause
        );
    }
}
