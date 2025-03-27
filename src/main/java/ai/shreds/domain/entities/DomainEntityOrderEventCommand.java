package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainInventoryException;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.shared.dtos.SharedOrderEventDTO;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an order event command that affects inventory quantities.
 * This entity encapsulates the logic for converting order events into inventory updates.
 */
public class DomainEntityOrderEventCommand {
    private final UUID orderId;
    private final UUID itemId;
    private final int quantity;

    private DomainEntityOrderEventCommand(Builder builder) {
        validateBuilderInputs(builder);
        this.orderId = builder.orderId;
        this.itemId = builder.itemId;
        this.quantity = builder.quantity;
    }

    private void validateBuilderInputs(Builder builder) {
        if (builder.orderId == null) {
            throw new DomainInventoryException("Order ID cannot be null");
        }
        if (builder.itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        if (builder.quantity <= 0) {
            throw new DomainInventoryException("Order quantity must be positive. Received: " + builder.quantity);
        }
    }

    /**
     * Converts the order quantity into a negative DomainValueQuantity for inventory deduction.
     * @return DomainValueQuantity representing the inventory reduction
     */
    public DomainValueQuantity toInventoryUpdate() {
        // Convert positive order quantity to negative for inventory deduction
        return new DomainValueQuantity(-this.quantity);
    }

    /**
     * Creates a SharedOrderEventDTO from this domain entity.
     * @return SharedOrderEventDTO representing this order event
     */
    public SharedOrderEventDTO toSharedDTO() {
        return new SharedOrderEventDTO(
                this.orderId,
                this.itemId,
                this.quantity
        );
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntityOrderEventCommand that = (DomainEntityOrderEventCommand) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "DomainEntityOrderEventCommand{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }

    /**
     * Builder for DomainEntityOrderEventCommand.
     */
    public static class Builder {
        private UUID orderId;
        private UUID itemId;
        private int quantity;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder itemId(UUID itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public DomainEntityOrderEventCommand build() {
            return new DomainEntityOrderEventCommand(this);
        }
    }
}
