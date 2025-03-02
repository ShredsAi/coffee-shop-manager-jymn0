package ai.shreds.infrastructure.repositories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "transaction_log",
    indexes = {
        @Index(name = "idx_transaction_log_payment_id", columnList = "payment_id"),
        @Index(name = "idx_transaction_log_timestamp", columnList = "timestamp"),
        @Index(name = "idx_transaction_log_status_code", columnList = "status_code")
    }
)
public class InfrastructureTransactionLogJpaEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "log_id", updatable = false, nullable = false)
    private UUID logId;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "gateway_name", length = 50)
    private String gatewayName;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @NotNull
    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData;

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (logId == null) {
            logId = UUID.randomUUID();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public boolean isSuccess() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }

    public boolean isError() {
        return !isSuccess();
    }
}
