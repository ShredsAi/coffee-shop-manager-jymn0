package ai.shreds.infrastructure.external_services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRequest {
    @NotNull
    private UUID paymentId;

    @NotNull
    private UUID orderId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String paymentMethod;

    private String description;

    private String customerEmail;

    private String customerReference;

    @Builder.Default
    private boolean capture = true;

    private String returnUrl;

    private String cancelUrl;

    private String webhookUrl;

    private Map<String, String> metadata;

    private Map<String, Object> paymentMethodDetails;

    @Builder.Default
    private String idempotencyKey = UUID.randomUUID().toString();

    private String statementDescriptor;

    private String merchantReference;

    @Builder.Default
    private boolean threeDSecureRequired = true;

    private Map<String, Object> riskData;

    private String ipAddress;

    private String userAgent;
}
