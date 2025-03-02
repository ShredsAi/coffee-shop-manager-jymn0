package ai.shreds.infrastructure.repositories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "payment",
    indexes = {
        @Index(name = "idx_payment_order_id", columnList = "order_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_created_at", columnList = "created_at")
    }
)
public class InfrastructurePaymentJpaEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
