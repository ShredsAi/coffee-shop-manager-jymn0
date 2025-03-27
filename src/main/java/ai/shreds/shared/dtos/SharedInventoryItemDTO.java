package ai.shreds.shared.dtos;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.value_objects.DomainValueThreshold;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedInventoryItemDTO {

    @NotNull(message = "Item ID cannot be null")
    private UUID itemId;

    @NotBlank(message = "Item name cannot be empty")
    private String itemName;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Threshold cannot be null")
    @Min(value = 0, message = "Threshold cannot be negative")
    private Integer threshold;

    @NotNull(message = "Updated timestamp cannot be null")
    private Timestamp updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static SharedInventoryItemDTO fromDomainEntity(DomainEntityInventoryItem entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Domain entity cannot be null");
        }
        return builder()
                .itemId(entity.getItemId())
                .itemName(entity.getItemName())
                .quantity(entity.getQuantity().getValue())
                .threshold(entity.getThreshold().getValue())
                .updatedAt(Timestamp.from(entity.getUpdatedAt()))
                .build();
    }

    public DomainEntityInventoryItem toDomainEntity() {
        return new DomainEntityInventoryItem.Builder()
                .itemId(this.itemId)
                .itemName(this.itemName)
                .quantity(new DomainValueQuantity(this.quantity))
                .threshold(new DomainValueThreshold(this.threshold))
                .updatedAt(this.updatedAt.toInstant())
                .build();
    }

    public static class Builder {
        private final SharedInventoryItemDTO instance = new SharedInventoryItemDTO();

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

        public SharedInventoryItemDTO build() {
            return instance;
        }
    }
}
