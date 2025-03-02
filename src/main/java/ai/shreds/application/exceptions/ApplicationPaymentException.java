package ai.shreds.application.exceptions;

import lombok.Getter;

@Getter
public class ApplicationPaymentException extends RuntimeException {

    private final String errorCode;
    private final ErrorCategory errorCategory;

    public enum ErrorCategory {
        VALIDATION_ERROR("PAY_VAL_"),
        PROCESSING_ERROR("PAY_PROC_"),
        NOTIFICATION_ERROR("PAY_NOT_"),
        SYSTEM_ERROR("PAY_SYS_");

        private final String prefix;

        ErrorCategory(String prefix) {
            this.prefix = prefix;
        }

        public String getFullErrorCode(String code) {
            return this.prefix + code;
        }
    }

    public ApplicationPaymentException(String message) {
        this(message, ErrorCategory.SYSTEM_ERROR, "001");
    }

    public ApplicationPaymentException(String message, Throwable cause) {
        this(message, ErrorCategory.SYSTEM_ERROR, "001", cause);
    }

    public ApplicationPaymentException(String message, ErrorCategory category, String code) {
        super(message);
        this.errorCategory = category;
        this.errorCode = category.getFullErrorCode(code);
    }

    public ApplicationPaymentException(String message, ErrorCategory category, String code, Throwable cause) {
        super(message, cause);
        this.errorCategory = category;
        this.errorCode = category.getFullErrorCode(code);
    }

    // Convenience factory methods for common error scenarios
    public static ApplicationPaymentException validationError(String message) {
        return new ApplicationPaymentException(message, ErrorCategory.VALIDATION_ERROR, "001");
    }

    public static ApplicationPaymentException paymentProcessingError(String message) {
        return new ApplicationPaymentException(message, ErrorCategory.PROCESSING_ERROR, "001");
    }

    public static ApplicationPaymentException notificationError(String message, Throwable cause) {
        return new ApplicationPaymentException(message, ErrorCategory.NOTIFICATION_ERROR, "001", cause);
    }

    public static ApplicationPaymentException systemError(String message, Throwable cause) {
        return new ApplicationPaymentException(message, ErrorCategory.SYSTEM_ERROR, "001", cause);
    }

    @Override
    public String toString() {
        return String.format("ApplicationPaymentException[%s]: %s", errorCode, getMessage());
    }
}
