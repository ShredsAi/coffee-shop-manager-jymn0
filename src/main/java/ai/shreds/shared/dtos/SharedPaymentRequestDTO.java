package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotBlank;
import java.util.UUID;
import java.math.BigDecimal;
import ai.shreds.domain.value_objects.DomainPaymentRequest;

/**
 * Data Transfer Object for payment requests.
 * Validates and transfers payment request data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedPaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /**
     * Converts this DTO to a domain payment request object.
     * This method ensures proper data transformation between layers.
     *
     * @return A new DomainPaymentRequest populated with this DTO's data
     */
    public DomainPaymentRequest toDomainPaymentRequest() {
        return DomainPaymentRequest.builder()
                .orderId(this.orderId)
                .amount(this.amount)
                .paymentMethod(this.paymentMethod)
                .build();
    }

    /**
     * Validates the payment request data.
     * This method performs business rule validations beyond simple annotations.
     *
     * @throws IllegalArgumentException if any business rule is violated
     */
    public void validate() {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (paymentMethod != null && !isValidPaymentMethod(paymentMethod)) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }
    }

    private boolean isValidPaymentMethod(String method) {
        return method.matches("^(CREDIT_CARD|DEBIT_CARD|BANK_TRANSFER|MOBILE_WALLET)$");
    }
}
