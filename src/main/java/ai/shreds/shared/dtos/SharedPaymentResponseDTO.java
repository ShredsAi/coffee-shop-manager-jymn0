package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import ai.shreds.domain.entities.DomainPaymentEntity;

/**
 * Data Transfer Object for payment responses.
 * This DTO represents the response sent back after processing a payment request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedPaymentResponseDTO {

    private UUID paymentId;
    private String status;
    private UUID orderId;
    private String message;

    /**
     * Creates a SharedPaymentResponseDTO from a domain payment entity.
     *
     * @param payment The domain payment entity to convert
     * @return The populated SharedPaymentResponseDTO
     */
    public static SharedPaymentResponseDTO fromDomainPayment(DomainPaymentEntity payment) {
        return SharedPaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .status(payment.getStatus().name())
                .orderId(payment.getOrderId())
                .message(generateStatusMessage(payment.getStatus().name()))
                .build();
    }

    /**
     * Converts the DTO to a ResponseEntity for HTTP responses.
     *
     * @return ResponseEntity containing the DTO
     */
    public ResponseEntity<SharedPaymentResponseDTO> toResponse() {
        HttpStatus httpStatus = determineHttpStatus(this.status);
        return new ResponseEntity<>(this, httpStatus);
    }

    private static String generateStatusMessage(String status) {
        switch (status) {
            case "SUCCESS":
                return "Payment processed successfully";
            case "PENDING":
                return "Payment is being processed";
            case "FAILURE":
                return "Payment processing failed";
            default:
                return "Payment status: " + status;
        }
    }

    private HttpStatus determineHttpStatus(String status) {
        switch (status) {
            case "SUCCESS":
                return HttpStatus.OK;
            case "PENDING":
                return HttpStatus.ACCEPTED;
            case "FAILURE":
                return HttpStatus.PAYMENT_REQUIRED;
            default:
                return HttpStatus.OK;
        }
    }
}