package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedOrderEventDTO;
import ai.shreds.application.exceptions.ApplicationInventoryException;

/**
 * Application port for handling order events that affect inventory.
 * Provides methods to process order-related events and update inventory accordingly.
 */
public interface ApplicationOrderEventInputPort {

    /**
     * Handles an order event by updating the inventory accordingly.
     * This method processes events such as order placement, cancellation, or modification
     * that affect inventory levels.
     *
     * @param event The order event containing details about the inventory change
     * @throws ApplicationInventoryException if the event cannot be processed or inventory update fails
     */
    void handleOrderEvent(SharedOrderEventDTO event) throws ApplicationInventoryException;

    /**
     * Validates an order event before processing.
     * This method ensures the event contains valid data and can be processed.
     *
     * @param event The order event to validate
     * @return true if the event is valid and can be processed
     * @throws ApplicationInventoryException if the event is invalid
     */
    boolean validateOrderEvent(SharedOrderEventDTO event) throws ApplicationInventoryException;

    /**
     * Checks if there is sufficient inventory to process the order event.
     * This method verifies that the requested quantity is available before processing the event.
     *
     * @param event The order event to check
     * @return true if sufficient inventory is available
     * @throws ApplicationInventoryException if the inventory check fails
     */
    boolean checkInventoryAvailability(SharedOrderEventDTO event) throws ApplicationInventoryException;

    /**
     * Rolls back an order event if processing fails.
     * This method ensures inventory consistency in case of failures during event processing.
     *
     * @param event The order event to roll back
     * @throws ApplicationInventoryException if the rollback fails
     */
    void rollbackOrderEvent(SharedOrderEventDTO event) throws ApplicationInventoryException;
}
