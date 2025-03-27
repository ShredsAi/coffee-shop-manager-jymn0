package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainInventoryException;

import java.util.Objects;

/**
 * Value object representing a quantity in the inventory domain.
 * This class encapsulates all validation and business rules for quantity values.
 * It is immutable and validates the quantity during construction.
 */
public final class DomainValueQuantity {

    private final int value;

    /**
     * Creates a new quantity value object.
     * @param value the quantity value
     * @throws DomainInventoryException if the quantity is negative
     */
    public DomainValueQuantity(int value) {
        validateQuantity(value);
        this.value = value;
    }

    private void validateQuantity(int value) {
        if (value < 0) {
            throw new DomainInventoryException("Quantity cannot be negative. Received: " + value);
        }
    }

    /**
     * Gets the quantity value.
     * @return the validated quantity value
     */
    public int getValue() {
        return value;
    }

    /**
     * Adds a quantity to this one, creating a new value object.
     * @param other the quantity to add
     * @return a new DomainValueQuantity with the sum
     * @throws DomainInventoryException if the result would be negative
     */
    public DomainValueQuantity add(DomainValueQuantity other) {
        if (other == null) {
            throw new DomainInventoryException("Cannot add null quantity");
        }
        return new DomainValueQuantity(this.value + other.value);
    }

    /**
     * Subtracts a quantity from this one, creating a new value object.
     * @param other the quantity to subtract
     * @return a new DomainValueQuantity with the difference
     * @throws DomainInventoryException if the result would be negative
     */
    public DomainValueQuantity subtract(DomainValueQuantity other) {
        if (other == null) {
            throw new DomainInventoryException("Cannot subtract null quantity");
        }
        return new DomainValueQuantity(this.value - other.value);
    }

    /**
     * Checks if this quantity is less than another quantity.
     * @param other the quantity to compare with
     * @return true if this quantity is less than the other
     */
    public boolean isLessThan(DomainValueQuantity other) {
        if (other == null) {
            throw new DomainInventoryException("Cannot compare with null quantity");
        }
        return this.value < other.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueQuantity that = (DomainValueQuantity) o;
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
