package ai.shreds.infrastructure.external_services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private UUID notificationId;
    private String type;
    private UUID paymentId;
    private UUID orderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private LocalDateTime timestamp;
    private String serviceName;
    private String eventType;
    private Map<String, Object> additionalData;
    private String errorCode;
    private String errorMessage;
    private Integer retryCount;
    private String version;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static NotificationMessage createOrderNotification(UUID paymentId, UUID orderId, String status, 
            BigDecimal amount, String currency, String paymentMethod) {
        return NotificationMessage.builder()
            .notificationId(UUID.randomUUID())
            .type("ORDER_NOTIFICATION")
            .paymentId(paymentId)
            .orderId(orderId)
            .status(status)
            .amount(amount)
            .currency(currency)
            .paymentMethod(paymentMethod)
            .timestamp(LocalDateTime.now())
            .serviceName("payment-service")
            .eventType("PAYMENT_STATUS_UPDATE")
            .version("1.0")
            .build();
    }

    public static NotificationMessage createFinancialNotification(UUID paymentId, UUID orderId, 
            BigDecimal amount, String currency, String paymentMethod) {
        return NotificationMessage.builder()
            .notificationId(UUID.randomUUID())
            .type("FINANCIAL_NOTIFICATION")
            .paymentId(paymentId)
            .orderId(orderId)
            .amount(amount)
            .currency(currency)
            .paymentMethod(paymentMethod)
            .timestamp(LocalDateTime.now())
            .serviceName("payment-service")
            .eventType("PAYMENT_PROCESSED")
            .version("1.0")
            .build();
    }
}
