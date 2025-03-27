package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainInventoryException;

public class DomainQuantityValue {
    private final Integer value;

    public DomainQuantityValue(Integer value) {
        this.value = value;
        validate();
    }

    public Integer getValue() {
        return value;
    }

    private void validate() {
        if (value == null) {
            throw new DomainInventoryException("Quantity value cannot be null");
        }
        if (value < 0) {
            throw new DomainInventoryException("Quantity value cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainQuantityValue that = (DomainQuantityValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}