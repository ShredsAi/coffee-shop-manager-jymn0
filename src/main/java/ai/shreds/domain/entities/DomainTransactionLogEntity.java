package ai.shreds.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class DomainTransactionLogEntity {
    private final UUID logId;
    private final UUID paymentId;
    private final String gatewayResponse;
    private final Integer statusCode;
    private final String message;
    private final LocalDateTime timestamp;

    private DomainTransactionLogEntity(Builder builder) {
        validateBuilder(builder);
        this.logId = builder.logId;
        this.paymentId = builder.paymentId;
        this.gatewayResponse = sanitizeGatewayResponse(builder.gatewayResponse);
        this.statusCode = builder.statusCode;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DomainTransactionLogEntity createSuccessLog(UUID paymentId, String gatewayResponse, String message) {
        return builder()
                .paymentId(paymentId)
                .gatewayResponse(gatewayResponse)
                .statusCode(200)
                .message(message)
                .build();
    }

    public static DomainTransactionLogEntity createFailureLog(UUID paymentId, String gatewayResponse, Integer statusCode, String errorMessage) {
        return builder()
                .paymentId(paymentId)
                .gatewayResponse(gatewayResponse)
                .statusCode(statusCode)
                .message(errorMessage)
                .build();
    }

    private static void validateBuilder(Builder builder) {
        if (builder.paymentId == null) {
            throw new IllegalArgumentException("PaymentId cannot be null");
        }
        if (builder.statusCode == null) {
            throw new IllegalArgumentException("StatusCode cannot be null");
        }
        if (builder.timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (builder.timestamp.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Timestamp cannot be in the future");
        }
    }

    private String sanitizeGatewayResponse(String response) {
        if (response == null) {
            return null;
        }
        // Remove sensitive data patterns (credit card numbers, CVV, etc.)
        response = response.replaceAll("\b\d{16}\b", "****-****-****-****");
        response = response.replaceAll("\b\d{3}\b", "***");
        // Limit response size
        return response.length() > 4000 ? response.substring(0, 4000) : response;
    }

    // Getters
    public UUID getLogId() { return logId; }
    public UUID getPaymentId() { return paymentId; }
    public String getGatewayResponse() { return gatewayResponse; }
    public Integer getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainTransactionLogEntity that = (DomainTransactionLogEntity) o;
        return Objects.equals(logId, that.logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }

    @Override
    public String toString() {
        return "DomainTransactionLogEntity{" +
                "logId=" + logId +
                ", paymentId=" + paymentId +
                ", statusCode=" + statusCode +
                ", message='" + message + ''' +
                ", timestamp=" + timestamp +
                // Excluding gatewayResponse from toString for security
                '}';
    }

    public static class Builder {
        private UUID logId = UUID.randomUUID();
        private UUID paymentId;
        private String gatewayResponse;
        private Integer statusCode;
        private String message;
        private LocalDateTime timestamp = LocalDateTime.now();

        private Builder() {}

        public Builder logId(UUID logId) {
            this.logId = logId;
            return this;
        }

        public Builder paymentId(UUID paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder gatewayResponse(String gatewayResponse) {
            this.gatewayResponse = gatewayResponse;
            return this;
        }

        public Builder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public DomainTransactionLogEntity build() {
            return new DomainTransactionLogEntity(this);
        }
    }
}
