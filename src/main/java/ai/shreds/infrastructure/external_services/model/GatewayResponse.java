package ai.shreds.infrastructure.external_services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResponse {
    private boolean success;
    private String transactionId;
    private String gatewayReference;
    private String message;
    private String errorCode;
    private String errorDetails;
    private Integer statusCode;
    private BigDecimal processedAmount;
    private String currency;
    private LocalDateTime processingTime;
    private Long processingDurationMs;
    private String rawResponse;
    private String paymentMethod;
    private String paymentMethodDetails;
    private boolean requiresAction;
    private String actionUrl;
    private String actionType;
}
