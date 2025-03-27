package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.exceptions.DomainInventoryException;
import ai.shreds.domain.ports.DomainInputPortInventory;
import ai.shreds.domain.ports.DomainOutputPortInventoryRepository;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Core domain service implementing inventory management business logic.
 * This service handles inventory operations and maintains business rules.
 */
@Service
public class DomainServiceInventory implements DomainInputPortInventory {

    private final DomainOutputPortInventoryRepository repository;
    private final DomainServiceLowSupplyAlert alertService;

    public DomainServiceInventory(
            DomainOutputPortInventoryRepository repository,
            DomainServiceLowSupplyAlert alertService
    ) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.alertService = Objects.requireNonNull(alertService, "Alert service cannot be null");
    }

    @Override
    public List<DomainEntityInventoryItem> getAllItems() {
        return repository.findAll();
    }

    @Override
    public DomainEntityInventoryItem getItemById(UUID itemId) {
        if (itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        return repository.findById(itemId)
                .orElseThrow(() -> new DomainInventoryException("Item not found with ID: " + itemId));
    }

    @Override
    public DomainEntityInventoryItem updateItemQuantity(UUID itemId, DomainValueQuantity quantity) {
        if (itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        if (quantity == null) {
            throw new DomainInventoryException("Quantity cannot be null");
        }

        DomainEntityInventoryItem item = getItemById(itemId);
        item.updateQuantity(quantity);
        DomainEntityInventoryItem updated = repository.save(item);
        checkAndGenerateAlert(updated);
        return updated;
    }

    @Override
    public void processOrderEvent(UUID itemId, DomainValueQuantity quantity) {
        if (itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        if (quantity == null) {
            throw new DomainInventoryException("Quantity cannot be null");
        }

        DomainEntityInventoryItem item = getItemById(itemId);
        
        // Ensure there's enough stock for the order
        if (item.getQuantity().getValue() < Math.abs(quantity.getValue())) {
            throw new DomainInventoryException("Insufficient stock for item: " + itemId + 
                    ". Required: " + Math.abs(quantity.getValue()) + 
                    ", Available: " + item.getQuantity().getValue());
        }

        // Create a new quantity that represents the reduction in stock
        DomainValueQuantity newQuantity = new DomainValueQuantity(
                item.getQuantity().getValue() + quantity.getValue() // quantity.getValue() is negative for orders
        );

        item.updateQuantity(newQuantity);
        DomainEntityInventoryItem updated = repository.save(item);
        checkAndGenerateAlert(updated);
    }

    private void checkAndGenerateAlert(DomainEntityInventoryItem item) {
        if (item == null) {
            throw new DomainInventoryException("Cannot check alerts for null item");
        }
        if (item.isBelowThreshold()) {
            alertService.generateAlert(item);
        }
    }
}