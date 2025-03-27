package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import ai.shreds.domain.ports.DomainOutputPortInventoryRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureDatabaseException;
import ai.shreds.infrastructure.mappers.InfrastructureInventoryMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Transactional
public class InfrastructureInventoryRepositoryImpl implements DomainOutputPortInventoryRepository {

    private final InfrastructureInventoryJpaRepository inventoryJpaRepository;
    private final InfrastructureLowSupplyAlertJpaRepository alertRepository;
    private final InfrastructureInventoryMapper mapper;

    public InfrastructureInventoryRepositoryImpl(
            InfrastructureInventoryJpaRepository inventoryJpaRepository,
            InfrastructureLowSupplyAlertJpaRepository alertRepository,
            InfrastructureInventoryMapper mapper
    ) {
        this.inventoryJpaRepository = inventoryJpaRepository;
        this.alertRepository = alertRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityInventoryItem> findAll() {
        try {
            return inventoryJpaRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw InfrastructureDatabaseException.fromJpaException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomainEntityInventoryItem> findById(UUID itemId) {
        try {
            return inventoryJpaRepository.findById(itemId)
                .map(mapper::toDomainEntity);
        } catch (Exception e) {
            throw InfrastructureDatabaseException.fromJpaException(e);
        }
    }

    @Override
    @Transactional
    public DomainEntityInventoryItem save(DomainEntityInventoryItem item) {
        try {
            var jpaEntity = mapper.toJpaEntity(item);
            var savedJpa = inventoryJpaRepository.save(jpaEntity);
            return mapper.toDomainEntity(savedJpa);
        } catch (OptimisticLockException e) {
            throw new InfrastructureDatabaseException(
                "Concurrent modification detected while saving inventory item",
                "CONCURRENT_MODIFICATION",
                e
            );
        } catch (Exception e) {
            throw InfrastructureDatabaseException.fromJpaException(e);
        }
    }

    @Override
    @Transactional
    public void saveAlert(DomainEntityLowSupplyAlert alert) {
        try {
            var jpaAlert = mapper.toJpaAlertEntity(alert);
            alertRepository.save(jpaAlert);
        } catch (OptimisticLockException e) {
            throw new InfrastructureDatabaseException(
                "Concurrent modification detected while saving alert",
                "CONCURRENT_MODIFICATION",
                e
            );
        } catch (Exception e) {
            throw InfrastructureDatabaseException.fromJpaException(e);
        }
    }
}