package ai.shreds.domain.value_objects;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Value object representing a payment request in the domain layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainPaymentRequest {
    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
}