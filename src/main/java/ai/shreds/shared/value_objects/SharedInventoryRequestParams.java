package ai.shreds.shared.value_objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Value object representing inventory update request parameters.
 * This class is immutable and thread-safe.
 */
public final class SharedInventoryRequestParams {

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    private final Integer quantity;

    private SharedInventoryRequestParams(Builder builder) {
        validateQuantity(builder.quantity);
        this.quantity = builder.quantity;
    }

    /**
     * Validates that the quantity is valid for inventory operations.
     *
     * @param quantity The quantity to validate
     * @throws IllegalArgumentException if quantity is null or negative
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    /**
     * Converts the request parameters to their internal representation.
     *
     * @return The validated internal quantity value
     * @throws IllegalStateException if conversion fails
     */
    public Integer toInternal() {
        try {
            if (quantity > Integer.MAX_VALUE - 100) { // Leave room for potential additions
                throw new IllegalStateException("Quantity is too large for internal processing");
            }
            return this.quantity;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert quantity to internal representation", e);
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedInventoryRequestParams that = (SharedInventoryRequestParams) o;
        return Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }

    @Override
    public String toString() {
        return "SharedInventoryRequestParams{" +
                "quantity=" + quantity +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer quantity;

        private Builder() {
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public SharedInventoryRequestParams build() {
            return new SharedInventoryRequestParams(this);
        }
    }
}
