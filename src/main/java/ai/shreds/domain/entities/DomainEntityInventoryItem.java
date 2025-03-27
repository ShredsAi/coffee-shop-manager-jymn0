package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainInventoryException;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.value_objects.DomainValueThreshold;
import ai.shreds.shared.dtos.SharedInventoryItemDTO;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Core domain entity representing an inventory item.
 * This entity encapsulates all the business rules and invariants for inventory items.
 */
public class DomainEntityInventoryItem {

    private final UUID itemId;
    private final String itemName;
    private DomainValueQuantity quantity;
    private final DomainValueThreshold threshold;
    private Instant updatedAt;

    private DomainEntityInventoryItem(Builder builder) {
        validateBuilderInputs(builder);
        this.itemId = builder.itemId;
        this.itemName = builder.itemName;
        this.quantity = builder.quantity;
        this.threshold = builder.threshold;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
    }

    private void validateBuilderInputs(Builder builder) {
        if (builder.itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        if (builder.itemName == null || builder.itemName.trim().isEmpty()) {
            throw new DomainInventoryException("Item name cannot be null or empty");
        }
        if (builder.quantity == null) {
            throw new DomainInventoryException("Quantity cannot be null");
        }
        if (builder.threshold == null) {
            throw new DomainInventoryException("Threshold cannot be null");
        }
    }

    /**
     * Updates the quantity of the inventory item.
     * @param newQuantity The new quantity value object
     * @throws DomainInventoryException if the new quantity is invalid
     */
    public void updateQuantity(DomainValueQuantity newQuantity) {
        if (newQuantity == null) {
            throw new DomainInventoryException("New quantity cannot be null");
        }
        this.quantity = newQuantity;
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if the current quantity is below the threshold.
     * @return true if the quantity is below threshold, false otherwise
     */
    public boolean isBelowThreshold() {
        return quantity.getValue() < threshold.getValue();
    }

    /**
     * Converts this domain entity to a shared DTO.
     * @return SharedInventoryItemDTO representing this entity
     */
    public SharedInventoryItemDTO toSharedDTO() {
        return new SharedInventoryItemDTO.Builder()
                .itemId(this.itemId)
                .itemName(this.itemName)
                .quantity(this.quantity.getValue())
                .threshold(this.threshold.getValue())
                .updatedAt(Timestamp.from(this.updatedAt))
                .build();
    }

    public UUID getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public DomainValueQuantity getQuantity() {
        return quantity;
    }

    public DomainValueThreshold getThreshold() {
        return threshold;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntityInventoryItem that = (DomainEntityInventoryItem) o;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    /**
     * Builder class for DomainEntityInventoryItem.
     */
    public static class Builder {
        private UUID itemId;
        private String itemName;
        private DomainValueQuantity quantity;
        private DomainValueThreshold threshold;
        private Instant updatedAt;

        public Builder itemId(UUID itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder quantity(DomainValueQuantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder threshold(DomainValueThreshold threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DomainEntityInventoryItem build() {
            return new DomainEntityInventoryItem(this);
        }
    }
}
