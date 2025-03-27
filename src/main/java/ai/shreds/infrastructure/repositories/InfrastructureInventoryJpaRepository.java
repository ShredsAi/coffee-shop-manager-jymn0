package ai.shreds.infrastructure.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ai.shreds.infrastructure.entities.InfrastructureInventoryItemJpaEntity;

public interface InfrastructureInventoryJpaRepository extends JpaRepository<InfrastructureInventoryItemJpaEntity, UUID> {
    
    @Query("SELECT i FROM InfrastructureInventoryItemJpaEntity i WHERE i.quantity <= i.threshold")
    List<InfrastructureInventoryItemJpaEntity> findAllBelowThreshold();
    
    @Query("SELECT i FROM InfrastructureInventoryItemJpaEntity i WHERE i.quantity <= :quantity")
    List<InfrastructureInventoryItemJpaEntity> findAllByQuantityLessThanEqual(@Param("quantity") Integer quantity);
    
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM InfrastructureInventoryItemJpaEntity i WHERE i.itemId = :itemId AND i.version = :version")
    boolean existsByIdAndVersion(@Param("itemId") UUID itemId, @Param("version") Long version);
}
