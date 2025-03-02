package ai.shreds.domain.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Domain-specific exception for payment processing errors.
 * This exception carries detailed error information and context.
 */
public class DomainPaymentException extends RuntimeException {

    private final String errorCode;
    private final ErrorCategory category;
    private final Map<String, Object> errorContext;

    /**
     * Error categories for better error handling and reporting.
     */
    public enum ErrorCategory {
        VALIDATION_ERROR("VAL"),
        PROCESSING_ERROR("PROC"),
        STATE_ERROR("STATE"),
        BUSINESS_RULE_VIOLATION("BRV"),
        SYSTEM_ERROR("SYS");

        private final String prefix;

        ErrorCategory(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * Creates a new domain payment exception with message and error code.
     *
     * @param message The error message
     * @param errorCode The specific error code
     */
    public DomainPaymentException(String message, String errorCode) {
        this(message, errorCode, ErrorCategory.SYSTEM_ERROR, null, null);
    }

    /**
     * Creates a new domain payment exception with message, error code, and cause.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param cause The cause of this exception
     */
    public DomainPaymentException(String message, String errorCode, Throwable cause) {
        this(message, errorCode, ErrorCategory.SYSTEM_ERROR, cause, null);
    }

    /**
     * Creates a new domain payment exception with all details.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param category The error category
     * @param cause The cause of this exception
     * @param context Additional context information
     */
    public DomainPaymentException(String message, String errorCode, ErrorCategory category, 
                                 Throwable cause, Map<String, Object> context) {
        super(message, cause);
        this.errorCode = category.getPrefix() + "-" + errorCode;
        this.category = category;
        this.errorContext = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    /**
     * Creates a validation error exception.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param context Validation context information
     * @return A new DomainPaymentException for validation errors
     */
    public static DomainPaymentException validationError(String message, String errorCode, 
                                                        Map<String, Object> context) {
        return new DomainPaymentException(message, errorCode, ErrorCategory.VALIDATION_ERROR, null, context);
    }

    /**
     * Creates a business rule violation exception.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param context Business rule context information
     * @return A new DomainPaymentException for business rule violations
     */
    public static DomainPaymentException businessRuleViolation(String message, String errorCode, 
                                                              Map<String, Object> context) {
        return new DomainPaymentException(message, errorCode, ErrorCategory.BUSINESS_RULE_VIOLATION, 
                                         null, context);
    }

    /**
     * Creates a processing error exception.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param cause The cause of the processing error
     * @param context Processing context information
     * @return A new DomainPaymentException for processing errors
     */
    public static DomainPaymentException processingError(String message, String errorCode, 
                                                        Throwable cause, Map<String, Object> context) {
        return new DomainPaymentException(message, errorCode, ErrorCategory.PROCESSING_ERROR, 
                                         cause, context);
    }

    /**
     * Creates a state error exception.
     *
     * @param message The error message
     * @param errorCode The specific error code
     * @param context State transition context information
     * @return A new DomainPaymentException for state errors
     */
    public static DomainPaymentException stateError(String message, String errorCode, 
                                                   Map<String, Object> context) {
        return new DomainPaymentException(message, errorCode, ErrorCategory.STATE_ERROR, 
                                         null, context);
    }

    /**
     * Gets the error code with category prefix.
     *
     * @return The complete error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error category.
     *
     * @return The error category
     */
    public ErrorCategory getCategory() {
        return category;
    }

    /**
     * Gets the error context information.
     *
     * @return Unmodifiable map of error context
     */
    public Map<String, Object> getErrorContext() {
        return new HashMap<>(errorContext);
    }

    /**
     * Adds context information to the error.
     *
     * @param key The context key
     * @param value The context value
     */
    public void addContext(String key, Object value) {
        errorContext.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("DomainPaymentException[%s]: %s (Category: %s, Context: %s)", 
                            errorCode, getMessage(), category, errorContext);
    }
}
