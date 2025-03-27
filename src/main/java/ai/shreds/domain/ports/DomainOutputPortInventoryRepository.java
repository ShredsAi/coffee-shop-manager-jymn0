package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainOutputPortInventoryRepository {

    List<DomainEntityInventoryItem> findAll();

    Optional<DomainEntityInventoryItem> findById(UUID itemId);

    DomainEntityInventoryItem save(DomainEntityInventoryItem item);

    void saveAlert(DomainEntityLowSupplyAlert alert);
}
