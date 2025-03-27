package ai.shreds.infrastructure.entities;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.value_objects.DomainValueThreshold;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "inventory_items",
    indexes = {
        @Index(name = "idx_inventory_quantity", columnList = "quantity"),
        @Index(name = "idx_inventory_threshold", columnList = "threshold")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureInventoryItemJpaEntity {

    @Id
    @Column(name = "item_id", nullable = false, updatable = false)
    private UUID itemId;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "threshold", nullable = false)
    private Integer threshold;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (updatedAt == null) {
            updatedAt = new Timestamp(System.currentTimeMillis());
        }
        if (itemId == null) {
            itemId = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts this JPA entity to a DomainEntityInventoryItem.
     * @return DomainEntityInventoryItem instance
     */
    public DomainEntityInventoryItem toDomainEntity() {
        return new DomainEntityInventoryItem.Builder()
                .itemId(this.itemId)
                .itemName(this.itemName)
                .quantity(new DomainValueQuantity(this.quantity))
                .threshold(new DomainValueThreshold(this.threshold))
                .updatedAt(this.updatedAt.toInstant())
                .build();
    }

    /**
     * Creates a JPA entity from a domain entity.
     * @param domainEntity the domain entity to convert
     * @return InfrastructureInventoryItemJpaEntity instance
     */
    public static InfrastructureInventoryItemJpaEntity fromDomainEntity(DomainEntityInventoryItem domainEntity) {
        return builder()
                .itemId(domainEntity.getItemId())
                .itemName(domainEntity.getItemName())
                .quantity(domainEntity.getQuantity().getValue())
                .threshold(domainEntity.getThreshold().getValue())
                .updatedAt(Timestamp.from(domainEntity.getUpdatedAt()))
                .build();
    }

    public static class Builder {
        private final InfrastructureInventoryItemJpaEntity instance = new InfrastructureInventoryItemJpaEntity();

        public Builder itemId(UUID itemId) {
            instance.setItemId(itemId);
            return this;
        }

        public Builder itemName(String itemName) {
            instance.setItemName(itemName);
            return this;
        }

        public Builder quantity(Integer quantity) {
            instance.setQuantity(quantity);
            return this;
        }

        public Builder threshold(Integer threshold) {
            instance.setThreshold(threshold);
            return this;
        }

        public Builder updatedAt(Timestamp updatedAt) {
            instance.setUpdatedAt(updatedAt);
            return this;
        }

        public Builder version(Long version) {
            instance.setVersion(version);
            return this;
        }

        public InfrastructureInventoryItemJpaEntity build() {
            return instance;
        }
    }
}
