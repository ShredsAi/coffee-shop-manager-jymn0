package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import java.util.List;
import java.util.UUID;

public interface DomainInputPortInventory {

    List<DomainEntityInventoryItem> getAllItems();

    DomainEntityInventoryItem getItemById(UUID itemId);

    DomainEntityInventoryItem updateItemQuantity(UUID itemId, DomainValueQuantity quantity);

    void processOrderEvent(UUID itemId, DomainValueQuantity quantity);
}
