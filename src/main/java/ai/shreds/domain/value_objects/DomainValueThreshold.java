package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainInventoryException;

import java.util.Objects;

/**
 * Value object representing a threshold value in the inventory domain.
 * This class encapsulates all validation and business rules for threshold values.
 * It is immutable and validates the threshold during construction.
 */
public final class DomainValueThreshold {

    private final int value;

    /**
     * Creates a new threshold value object.
     * @param value the threshold value
     * @throws DomainInventoryException if the threshold is negative
     */
    public DomainValueThreshold(int value) {
        validateThreshold(value);
        this.value = value;
    }

    private void validateThreshold(int value) {
        if (value < 0) {
            throw new DomainInventoryException("Threshold cannot be negative. Received: " + value);
        }
    }

    /**
     * Gets the threshold value.
     * @return the validated threshold value
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if a quantity is below this threshold.
     * @param quantity the quantity to check
     * @return true if the quantity is below this threshold
     */
    public boolean isQuantityBelowThreshold(DomainValueQuantity quantity) {
        if (quantity == null) {
            throw new DomainInventoryException("Cannot compare with null quantity");
        }
        return quantity.getValue() < this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueThreshold that = (DomainValueThreshold) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
