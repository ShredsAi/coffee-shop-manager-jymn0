package ai.shreds.domain.exceptions;

/**
 * Domain-specific exception for handling inventory-related business rule violations.
 * This exception is thrown when inventory operations violate domain rules or constraints.
 */
public class DomainInventoryException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message explaining the error condition
     */
    public DomainInventoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message explaining the error condition
     * @param cause the underlying cause of this exception
     */
    public DomainInventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception for when an item is not found.
     * @param itemId the ID of the item that wasn't found
     * @return a new DomainInventoryException with appropriate message
     */
    public static DomainInventoryException itemNotFound(String itemId) {
        return new DomainInventoryException(String.format("Item not found with ID: %s", itemId));
    }

    /**
     * Creates an exception for when a quantity would become negative.
     * @param currentQuantity the current quantity
     * @param requestedChange the requested change that would make it negative
     * @return a new DomainInventoryException with appropriate message
     */
    public static DomainInventoryException negativeQuantity(int currentQuantity, int requestedChange) {
        return new DomainInventoryException(
                String.format("Cannot reduce quantity %d by %d as it would result in negative stock",
                        currentQuantity, Math.abs(requestedChange)));
    }

    /**
     * Creates an exception for when there is insufficient stock for an order.
     * @param itemId the ID of the item
     * @param available the available quantity
     * @param requested the requested quantity
     * @return a new DomainInventoryException with appropriate message
     */
    public static DomainInventoryException insufficientStock(String itemId, int available, int requested) {
        return new DomainInventoryException(
                String.format("Insufficient stock for item %s. Available: %d, Requested: %d",
                        itemId, available, requested));
    }
}
