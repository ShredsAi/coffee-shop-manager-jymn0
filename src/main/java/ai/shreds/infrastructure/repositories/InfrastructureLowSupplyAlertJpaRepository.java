package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ai.shreds.infrastructure.entities.InfrastructureLowSupplyAlertJpaEntity;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public interface InfrastructureLowSupplyAlertJpaRepository extends JpaRepository<InfrastructureLowSupplyAlertJpaEntity, UUID> {
    
    @Query("SELECT a FROM InfrastructureLowSupplyAlertJpaEntity a WHERE a.itemId = :itemId ORDER BY a.alertDate DESC")
    List<InfrastructureLowSupplyAlertJpaEntity> findByItemIdOrderByAlertDateDesc(@Param("itemId") UUID itemId);
    
    @Query("SELECT a FROM InfrastructureLowSupplyAlertJpaEntity a WHERE a.alertDate >= :startDate AND a.alertDate <= :endDate")
    List<InfrastructureLowSupplyAlertJpaEntity> findAlertsBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(a) > 0 FROM InfrastructureLowSupplyAlertJpaEntity a WHERE a.itemId = :itemId AND a.alertDate >= :since")
    boolean existsRecentAlertForItem(
        @Param("itemId") UUID itemId,
        @Param("since") LocalDateTime since
    );
    
    @Query("DELETE FROM InfrastructureLowSupplyAlertJpaEntity a WHERE a.alertDate < :beforeDate")
    void deleteAlertsOlderThan(@Param("beforeDate") LocalDateTime beforeDate);
}
