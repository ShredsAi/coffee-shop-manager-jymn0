package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainMoneyValue;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;
import ai.shreds.shared.dtos.SharedPaymentRequestDTO;
import ai.shreds.shared.dtos.SharedPaymentResponseDTO;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class DomainPaymentEntity {
    private final UUID paymentId;
    private final UUID orderId;
    private final DomainMoneyValue amount;
    private final String paymentMethod;
    private DomainPaymentStatusValue status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private DomainPaymentEntity(Builder builder) {
        validateBuilder(builder);
        this.paymentId = builder.paymentId;
        this.orderId = builder.orderId;
        this.amount = builder.amount;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DomainPaymentEntity fromSharedPaymentRequest(SharedPaymentRequestDTO dto) {
        return DomainPaymentEntity.builder()
                .paymentId(UUID.randomUUID())
                .orderId(dto.getOrderId())
                .amount(DomainMoneyValue.of(dto.getAmount(), "USD")) // Default currency, could be parameterized
                .paymentMethod(dto.getPaymentMethod())
                .status(DomainPaymentStatusValue.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public SharedPaymentResponseDTO toSharedPaymentResponse() {
        return SharedPaymentResponseDTO.builder()
                .paymentId(this.paymentId)
                .orderId(this.orderId)
                .status(this.status.name())
                .build();
    }

    public void updateStatus(DomainPaymentStatusValue newStatus) {
        validateStatusTransition(newStatus);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateStatusTransition(DomainPaymentStatusValue newStatus) {
        if (this.status == DomainPaymentStatusValue.SUCCESS || 
            this.status == DomainPaymentStatusValue.FAILURE) {
            throw new IllegalStateException("Cannot update status of a finalized payment");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
    }

    private static void validateBuilder(Builder builder) {
        if (builder.orderId == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        if (builder.amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (builder.paymentMethod == null || builder.paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }
        if (builder.status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    // Getters
    public UUID getPaymentId() { return paymentId; }
    public UUID getOrderId() { return orderId; }
    public DomainMoneyValue getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public DomainPaymentStatusValue getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainPaymentEntity that = (DomainPaymentEntity) o;
        return Objects.equals(paymentId, that.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "DomainPaymentEntity{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + ''' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class Builder {
        private UUID paymentId = UUID.randomUUID();
        private UUID orderId;
        private DomainMoneyValue amount;
        private String paymentMethod;
        private DomainPaymentStatusValue status = DomainPaymentStatusValue.PENDING;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        private Builder() {}

        public Builder paymentId(UUID paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder amount(DomainMoneyValue amount) {
            this.amount = amount;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder status(DomainPaymentStatusValue status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DomainPaymentEntity build() {
            return new DomainPaymentEntity(this);
        }
    }
}
