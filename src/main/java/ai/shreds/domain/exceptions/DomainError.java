package ai.shreds.domain.exceptions;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain error representation for handling business and system errors.
 * Provides comprehensive error handling and categorization for the domain layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainError {
    private String code;
    private String message;
    private String remediationInfo;
    private LocalDateTime timestamp;
    private ErrorSeverity severity;
    private Map<String, Object> context;
    private String correlationId;

    /**
     * Error severity levels for better error handling and reporting.
     */
    public enum ErrorSeverity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    /**
     * Creates a validation error.
     *
     * @param message The validation error message
     * @param context Additional context information
     * @return A new DomainError instance
     */
    public static DomainError validationError(String message, Map<String, Object> context) {
        return DomainError.builder()
                .code("VAL-001")
                .message(message)
                .remediationInfo("Please check your input and try again")
                .timestamp(LocalDateTime.now())
                .severity(ErrorSeverity.WARNING)
                .context(context)
                .build();
    }

    /**
     * Creates a business rule violation error.
     *
     * @param message The business rule violation message
     * @param context Additional context information
     * @return A new DomainError instance
     */
    public static DomainError businessError(String message, Map<String, Object> context) {
        return DomainError.builder()
                .code("BUS-001")
                .message(message)
                .remediationInfo("Please ensure all business rules are satisfied")
                .timestamp(LocalDateTime.now())
                .severity(ErrorSeverity.ERROR)
                .context(context)
                .build();
    }

    /**
     * Creates a system error.
     *
     * @param message The system error message
     * @param context Additional context information
     * @return A new DomainError instance
     */
    public static DomainError systemError(String message, Map<String, Object> context) {
        return DomainError.builder()
                .code("SYS-001")
                .message(message)
                .remediationInfo("Please try again later or contact support")
                .timestamp(LocalDateTime.now())
                .severity(ErrorSeverity.CRITICAL)
                .context(context)
                .build();
    }

    /**
     * Creates a payment processing error.
     *
     * @param message The payment processing error message
     * @param context Additional context information
     * @return A new DomainError instance
     */
    public static DomainError paymentProcessingError(String message, Map<String, Object> context) {
        return DomainError.builder()
                .code("PAY-001")
                .message(message)
                .remediationInfo("Please verify payment details and try again")
                .timestamp(LocalDateTime.now())
                .severity(ErrorSeverity.ERROR)
                .context(context)
                .build();
    }

    /**
     * Creates an error from a DomainPaymentException.
     *
     * @param exception The domain payment exception
     * @return A new DomainError instance
     */
    public static DomainError fromDomainException(DomainPaymentException exception) {
        return DomainError.builder()
                .code(exception.getErrorCode())
                .message(exception.getMessage())
                .remediationInfo(generateRemediationInfo(exception.getCategory()))
                .timestamp(LocalDateTime.now())
                .severity(mapCategoryToSeverity(exception.getCategory()))
                .context(exception.getErrorContext())
                .build();
    }

    /**
     * Adds context information to the error.
     *
     * @param key The context key
     * @param value The context value
     * @return This DomainError instance for chaining
     */
    public DomainError addContext(String key, Object value) {
        if (this.context == null) {
            this.context = new HashMap<>();
        }
        this.context.put(key, value);
        return this;
    }

    /**
     * Sets the correlation ID for error tracking.
     *
     * @param correlationId The correlation ID
     * @return This DomainError instance for chaining
     */
    public DomainError withCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    private static String generateRemediationInfo(DomainPaymentException.ErrorCategory category) {
        switch (category) {
            case VALIDATION_ERROR:
                return "Please verify your input data and try again";
            case PROCESSING_ERROR:
                return "Payment processing failed. Please try again or contact support";
            case STATE_ERROR:
                return "Invalid payment state transition. Please check payment status";
            case BUSINESS_RULE_VIOLATION:
                return "Business rules violated. Please review payment requirements";
            default:
                return "An unexpected error occurred. Please contact support";
        }
    }

    private static ErrorSeverity mapCategoryToSeverity(DomainPaymentException.ErrorCategory category) {
        switch (category) {
            case VALIDATION_ERROR:
                return ErrorSeverity.WARNING;
            case PROCESSING_ERROR:
                return ErrorSeverity.ERROR;
            case STATE_ERROR:
                return ErrorSeverity.ERROR;
            case BUSINESS_RULE_VIOLATION:
                return ErrorSeverity.ERROR;
            default:
                return ErrorSeverity.CRITICAL;
        }
    }
}
