package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedInventoryItemDTO;
import ai.shreds.shared.value_objects.SharedInventoryRequestParams;
import ai.shreds.application.exceptions.ApplicationInventoryException;

import java.util.List;
import java.util.UUID;

/**
 * Application port for inventory management operations.
 * Provides high-level business operations for inventory manipulation.
 */
public interface ApplicationInventoryInputPort {

    /**
     * Retrieves all inventory items in the system.
     *
     * @return List of inventory items
     * @throws ApplicationInventoryException if there's an error retrieving the items
     */
    List<SharedInventoryItemDTO> getAllItems() throws ApplicationInventoryException;

    /**
     * Retrieves a specific inventory item by its ID.
     *
     * @param itemId The unique identifier of the item
     * @return The inventory item
     * @throws ApplicationInventoryException if the item is not found or there's an error retrieving it
     */
    SharedInventoryItemDTO getItemById(UUID itemId) throws ApplicationInventoryException;

    /**
     * Updates the quantity of an inventory item.
     *
     * @param itemId The unique identifier of the item to update
     * @param params The parameters containing the new quantity
     * @return The updated inventory item
     * @throws ApplicationInventoryException if the update fails or the item is not found
     */
    SharedInventoryItemDTO updateItemQuantity(UUID itemId, SharedInventoryRequestParams params) 
        throws ApplicationInventoryException;

    /**
     * Checks if an item has sufficient quantity available.
     *
     * @param itemId The unique identifier of the item to check
     * @param requiredQuantity The quantity needed
     * @return true if the item has sufficient quantity, false otherwise
     * @throws ApplicationInventoryException if the item is not found or there's an error checking availability
     */
    boolean checkItemAvailability(UUID itemId, int requiredQuantity) throws ApplicationInventoryException;
}
