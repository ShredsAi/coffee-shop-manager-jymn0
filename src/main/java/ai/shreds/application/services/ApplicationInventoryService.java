package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationInventoryInputPort;
import ai.shreds.application.ports.ApplicationOrderEventInputPort;
import ai.shreds.shared.dtos.SharedInventoryItemDTO;
import ai.shreds.shared.dtos.SharedOrderEventDTO;
import ai.shreds.shared.value_objects.SharedInventoryRequestParams;
import ai.shreds.domain.ports.DomainInputPortInventory;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.application.exceptions.ApplicationInventoryException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.dao.OptimisticLockingFailureException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApplicationInventoryService implements ApplicationInventoryInputPort, ApplicationOrderEventInputPort {

    private final DomainInputPortInventory domainInventoryPort;
    private final ApplicationInventoryMapper inventoryMapper;
    private final TransactionTemplate transactionTemplate;

    public ApplicationInventoryService(DomainInputPortInventory domainInventoryPort,
                                     ApplicationInventoryMapper inventoryMapper,
                                     TransactionTemplate transactionTemplate) {
        this.domainInventoryPort = domainInventoryPort;
        this.inventoryMapper = inventoryMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "inventory.get.all.items", description = "Time taken to retrieve all inventory items")
    public List<SharedInventoryItemDTO> getAllItems() {
        log.debug("Retrieving all inventory items");
        try {
            List<DomainEntityInventoryItem> domainItems = domainInventoryPort.getAllItems();
            List<SharedInventoryItemDTO> dtos = domainItems.stream()
                             .map(inventoryMapper::toDTO)
                             .collect(Collectors.toList());
            log.debug("Retrieved {} inventory items", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Error retrieving all inventory items", e);
            throw ApplicationInventoryException.systemError("Failed to retrieve inventory items", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "inventory.get.item.by.id", description = "Time taken to retrieve an inventory item by ID")
    public SharedInventoryItemDTO getItemById(UUID itemId) {
        if (itemId == null) {
            throw ApplicationInventoryException.invalidInput("Item ID cannot be null");
        }
        
        log.debug("Retrieving inventory item with ID: {}", itemId);
        try {
            DomainEntityInventoryItem domainItem = domainInventoryPort.getItemById(itemId);
            if (domainItem == null) {
                throw ApplicationInventoryException.notFound("Inventory item", itemId.toString());
            }
            SharedInventoryItemDTO dto = inventoryMapper.toDTO(domainItem);
            log.debug("Retrieved inventory item: {}", dto);
            return dto;
        } catch (ApplicationInventoryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving inventory item with ID: {}", itemId, e);
            throw ApplicationInventoryException.systemError("Failed to retrieve inventory item", e);
        }
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class,
               maxAttempts = 3,
               backoff = @Backoff(delay = 500))
    @Timed(value = "inventory.update.quantity", description = "Time taken to update inventory quantity")
    public SharedInventoryItemDTO updateItemQuantity(UUID itemId, SharedInventoryRequestParams params) {
        if (itemId == null) {
            throw ApplicationInventoryException.invalidInput("Item ID cannot be null");
        }
        validateInput(params);
        
        log.debug("Updating quantity for item ID: {} with params: {}", itemId, params);
        return transactionTemplate.execute(status -> {
            try {
                DomainValueQuantity quantityValue = new DomainValueQuantity(params.toInternal());
                DomainEntityInventoryItem updatedDomainItem = domainInventoryPort.updateItemQuantity(itemId, quantityValue);
                SharedInventoryItemDTO dto = inventoryMapper.toDTO(updatedDomainItem);
                log.debug("Successfully updated quantity for item ID: {}", itemId);
                return dto;
            } catch (OptimisticLockingFailureException e) {
                throw ApplicationInventoryException.concurrentModification(itemId.toString());
            } catch (Exception e) {
                throw ApplicationInventoryException.transactionError("update quantity", e.getMessage());
            }
        });
    }

    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class,
               maxAttempts = 3,
               backoff = @Backoff(delay = 500))
    @Timed(value = "inventory.handle.order.event", description = "Time taken to handle order event")
    public void handleOrderEvent(SharedOrderEventDTO event) {
        if (!validateOrderEvent(event)) {
            throw ApplicationInventoryException.invalidInput("Invalid order event");
        }

        if (!checkInventoryAvailability(event)) {
            throw ApplicationInventoryException.insufficientInventory(
                event.getItemId().toString(),
                event.getQuantity(),
                getItemById(event.getItemId()).getQuantity()
            );
        }

        log.debug("Handling order event: {}", event);
        transactionTemplate.execute(status -> {
            try {
                DomainValueQuantity quantityValue = new DomainValueQuantity(event.getQuantity());
                domainInventoryPort.processOrderEvent(event.getItemId(), quantityValue);
                log.debug("Successfully processed order event: {}", event);
                return null;
            } catch (OptimisticLockingFailureException e) {
                throw ApplicationInventoryException.concurrentModification(event.getItemId().toString());
            } catch (Exception e) {
                throw ApplicationInventoryException.transactionError("process order event", e.getMessage());
            }
        });
    }

    @Override
    @Timed(value = "inventory.check.availability", description = "Time taken to check item availability")
    public boolean checkItemAvailability(UUID itemId, int requiredQuantity) {
        if (itemId == null) {
            throw ApplicationInventoryException.invalidInput("Item ID cannot be null");
        }
        if (requiredQuantity < 0) {
            throw ApplicationInventoryException.invalidInput("Required quantity cannot be negative");
        }

        log.debug("Checking availability for item ID: {} with required quantity: {}", itemId, requiredQuantity);
        try {
            DomainEntityInventoryItem item = domainInventoryPort.getItemById(itemId);
            if (item == null) {
                throw ApplicationInventoryException.notFound("Inventory item", itemId.toString());
            }
            boolean isAvailable = item.getQuantity().getValue() >= requiredQuantity;
            log.debug("Availability check result for item ID: {}: {}", itemId, isAvailable);
            return isAvailable;
        } catch (ApplicationInventoryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking availability for item ID: {}", itemId, e);
            throw ApplicationInventoryException.systemError("Failed to check item availability", e);
        }
    }

    @Override
    public boolean validateOrderEvent(SharedOrderEventDTO event) {
        if (event == null) {
            log.debug("Order event validation failed: event is null");
            return false;
        }
        boolean isValid = event.getItemId() != null && 
                         event.getQuantity() > 0 && 
                         event.getOrderId() != null;
        log.debug("Order event validation result: {}", isValid);
        return isValid;
    }

    @Override
    public boolean checkInventoryAvailability(SharedOrderEventDTO event) {
        if (event == null) {
            throw ApplicationInventoryException.invalidInput("Order event cannot be null");
        }
        return checkItemAvailability(event.getItemId(), event.getQuantity());
    }

    @Override
    @Transactional
    @Timed(value = "inventory.rollback.order.event", description = "Time taken to rollback order event")
    public void rollbackOrderEvent(SharedOrderEventDTO event) {
        if (event == null) {
            throw ApplicationInventoryException.invalidInput("Order event cannot be null");
        }

        log.debug("Rolling back order event: {}", event);
        transactionTemplate.execute(status -> {
            try {
                DomainValueQuantity compensatingQuantity = new DomainValueQuantity(-event.getQuantity());
                domainInventoryPort.processOrderEvent(event.getItemId(), compensatingQuantity);
                log.debug("Successfully rolled back order event: {}", event);
                return null;
            } catch (OptimisticLockingFailureException e) {
                throw ApplicationInventoryException.concurrentModification(event.getItemId().toString());
            } catch (Exception e) {
                log.error("Critical: Failed to rollback order event: {}", event, e);
                throw ApplicationInventoryException.systemError("Failed to rollback order event", e);
            }
        });
    }

    private void validateInput(SharedInventoryRequestParams params) {
        log.debug("Validating input parameters: {}", params);
        if (params == null) {
            throw ApplicationInventoryException.invalidInput("Request parameters cannot be null");
        }
        if (params.toInternal() < 0) {
            throw ApplicationInventoryException.invalidInput("Quantity cannot be negative");
        }
    }
}
