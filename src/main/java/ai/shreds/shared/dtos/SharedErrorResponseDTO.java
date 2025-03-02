package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import ai.shreds.domain.exceptions.DomainError;

/**
 * Data Transfer Object for error responses.
 * Provides structured error information and remediation guidance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedErrorResponseDTO {

    private String errorCode;
    private String errorMessage;
    private String remediationInfo;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private String path;

    /**
     * Converts this DTO to a domain error object.
     *
     * @return A new DomainError populated with this DTO's data
     */
    public DomainError toDomainError() {
        return DomainError.builder()
                .code(this.errorCode)
                .message(this.errorMessage)
                .remediationInfo(this.remediationInfo)
                .timestamp(this.timestamp)
                .build();
    }

    /**
     * Creates a ResponseEntity containing this error DTO.
     *
     * @return ResponseEntity with appropriate HTTP status
     */
    public ResponseEntity<SharedErrorResponseDTO> toResponse() {
        return new ResponseEntity<>(this, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates an error response for validation failures.
     *
     * @param message The validation error message
     * @param path The request path where validation failed
     * @return A new SharedErrorResponseDTO
     */
    public static SharedErrorResponseDTO validationError(String message, String path) {
        return SharedErrorResponseDTO.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage(message)
                .remediationInfo("Please check the request parameters and try again")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Creates an error response for payment processing failures.
     *
     * @param message The payment processing error message
     * @param path The request path where processing failed
     * @return A new SharedErrorResponseDTO
     */
    public static SharedErrorResponseDTO paymentProcessingError(String message, String path) {
        return SharedErrorResponseDTO.builder()
                .errorCode("PAYMENT_PROCESSING_ERROR")
                .errorMessage(message)
                .remediationInfo("Please try again later or contact support if the issue persists")
                .status(HttpStatus.PAYMENT_REQUIRED)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Creates an error response for system errors.
     *
     * @param message The system error message
     * @param path The request path where the error occurred
     * @return A new SharedErrorResponseDTO
     */
    public static SharedErrorResponseDTO systemError(String message, String path) {
        return SharedErrorResponseDTO.builder()
                .errorCode("SYSTEM_ERROR")
                .errorMessage(message)
                .remediationInfo("An unexpected error occurred. Please try again later")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
