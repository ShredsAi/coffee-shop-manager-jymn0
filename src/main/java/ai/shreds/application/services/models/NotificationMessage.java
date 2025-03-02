package ai.shreds.application.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private UUID paymentId;
    private String eventType;
    private String status;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String serviceName;
    
    public static NotificationMessage forOrderService(UUID paymentId, String status) {
        return new NotificationMessage(
            paymentId,
            "PAYMENT_STATUS_UPDATE",
            status,
            null,
            LocalDateTime.now(),
            "ORDER_SERVICE"
        );
    }
    
    public static NotificationMessage forFinancialService(UUID paymentId, BigDecimal amount) {
        return new NotificationMessage(
            paymentId,
            "PAYMENT_FINANCIAL_UPDATE",
            null,
            amount,
            LocalDateTime.now(),
            "FINANCIAL_SERVICE"
        );
    }
}
