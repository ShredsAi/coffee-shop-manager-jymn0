package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainInventoryItemEntity;
import ai.shreds.domain.entities.DomainLowSupplyAlertEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainInventoryRepositoryOutputPort {
    List<DomainInventoryItemEntity> findAll();
    Optional<DomainInventoryItemEntity> findById(UUID itemId);
    DomainInventoryItemEntity save(DomainInventoryItemEntity item);
    void saveAlert(DomainLowSupplyAlertEntity alert);
}