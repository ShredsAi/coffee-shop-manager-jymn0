package ai.shreds.application.exceptions;

import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import lombok.Getter;

@Getter
public class ApplicationInventoryException extends RuntimeException {

    private final ErrorType errorType;
    private final String errorCode;

    public enum ErrorType {
        VALIDATION_ERROR("INV-001"),
        NOT_FOUND("INV-002"),
        INSUFFICIENT_INVENTORY("INV-003"),
        CONCURRENT_MODIFICATION("INV-004"),
        TRANSACTION_ERROR("INV-005"),
        MAPPING_ERROR("INV-006"),
        SYSTEM_ERROR("INV-999");

        private final String code;

        ErrorType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private ApplicationInventoryException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorType.getCode();
    }

    private ApplicationInventoryException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
        this.errorCode = errorType.getCode();
    }

    public SharedErrorResponseDTO toErrorResponse() {
        return new SharedErrorResponseDTO.Builder()
            .error(getMessage())
            .errorCode(errorCode)
            .details(errorType.name())
            .build();
    }

    public static ApplicationInventoryException fromDomainException(Exception e) {
        return new ApplicationInventoryException(
            "Application layer error: " + e.getMessage(),
            e,
            ErrorType.SYSTEM_ERROR
        );
    }

    public static ApplicationInventoryException invalidInput(String detail) {
        return new ApplicationInventoryException(
            "Invalid input: " + detail,
            ErrorType.VALIDATION_ERROR
        );
    }

    public static ApplicationInventoryException notFound(String entityType, String identifier) {
        return new ApplicationInventoryException(
            String.format("%s not found with identifier: %s", entityType, identifier),
            ErrorType.NOT_FOUND
        );
    }

    public static ApplicationInventoryException insufficientInventory(String itemId, int requested, int available) {
        return new ApplicationInventoryException(
            String.format("Insufficient inventory for item %s. Requested: %d, Available: %d", 
                itemId, requested, available),
            ErrorType.INSUFFICIENT_INVENTORY
        );
    }

    public static ApplicationInventoryException concurrentModification(String itemId) {
        return new ApplicationInventoryException(
            String.format("Concurrent modification detected for item %s", itemId),
            ErrorType.CONCURRENT_MODIFICATION
        );
    }

    public static ApplicationInventoryException transactionError(String operation, String detail) {
        return new ApplicationInventoryException(
            String.format("Transaction error during %s: %s", operation, detail),
            ErrorType.TRANSACTION_ERROR
        );
    }

    public static ApplicationInventoryException mappingError(String detail, Throwable cause) {
        return new ApplicationInventoryException(
            "Mapping error: " + detail,
            cause,
            ErrorType.MAPPING_ERROR
        );
    }

    public static ApplicationInventoryException systemError(String detail, Throwable cause) {
        return new ApplicationInventoryException(
            "System error: " + detail,
            cause,
            ErrorType.SYSTEM_ERROR
        );
    }
}