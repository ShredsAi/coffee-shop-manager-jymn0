package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import ai.shreds.domain.exceptions.DomainInventoryException;
import ai.shreds.domain.ports.DomainOutputPortInventoryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain service responsible for generating and managing low supply alerts.
 * This service handles the creation and persistence of alerts when inventory items fall below their thresholds.
 */
@Service
public class DomainServiceLowSupplyAlert {

    private final DomainOutputPortInventoryRepository repository;

    public DomainServiceLowSupplyAlert(DomainOutputPortInventoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    /**
     * Generates a low supply alert for an inventory item.
     * @param item The inventory item that has fallen below its threshold
     * @return The generated alert entity
     * @throws DomainInventoryException if the item is null or invalid
     */
    public DomainEntityLowSupplyAlert generateAlert(DomainEntityInventoryItem item) {
        validateItem(item);
        
        DomainEntityLowSupplyAlert alert = new DomainEntityLowSupplyAlert.Builder()
                .alertId(UUID.randomUUID())
                .itemId(item.getItemId())
                .alertDate(Instant.now())
                .alertMessage(formatAlertMessage(item))
                .build();

        repository.saveAlert(alert);
        return alert;
    }

    private void validateItem(DomainEntityInventoryItem item) {
        if (item == null) {
            throw new DomainInventoryException("Cannot generate alert for null item");
        }
        if (!item.isBelowThreshold()) {
            throw new DomainInventoryException("Cannot generate alert for item that is not below threshold: " + 
                    item.getItemId());
        }
    }

    private String formatAlertMessage(DomainEntityInventoryItem item) {
        return String.format("Low supply alert for %s: Current quantity %d is below threshold %d",
                item.getItemName(),
                item.getQuantity().getValue(),
                item.getThreshold().getValue());
    }
}