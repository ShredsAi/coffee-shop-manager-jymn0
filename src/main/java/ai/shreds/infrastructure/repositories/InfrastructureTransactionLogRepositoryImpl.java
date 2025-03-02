package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainTransactionLogEntity;
import ai.shreds.domain.ports.DomainTransactionLogPort;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional(readOnly = true)
public class InfrastructureTransactionLogRepositoryImpl implements DomainTransactionLogPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DomainTransactionLogEntity saveTransactionLog(@NotNull @Valid DomainTransactionLogEntity log) {
        try {
            log.debug("Saving transaction log for payment ID: {}", log.getPaymentId());
            InfrastructureTransactionLogJpaEntity jpaEntity = mapToJpaEntity(log);
            
            if (jpaEntity.getLogId() == null) {
                entityManager.persist(jpaEntity);
            } else {
                jpaEntity = entityManager.merge(jpaEntity);
            }
            
            entityManager.flush();
            log.info("Successfully saved transaction log with ID: {}", jpaEntity.getLogId());
            return mapToDomainEntity(jpaEntity);
        } catch (Exception e) {
            log.error("Error saving transaction log: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error saving TransactionLog", e);
        }
    }

    @Override
    public List<DomainTransactionLogEntity> findLogsByPaymentId(@NotNull UUID paymentId) {
        try {
            log.debug("Finding transaction logs for payment ID: {}", paymentId);
            TypedQuery<InfrastructureTransactionLogJpaEntity> query = entityManager.createQuery(
                "SELECT t FROM InfrastructureTransactionLogJpaEntity t WHERE t.paymentId = :paymentId ORDER BY t.timestamp DESC",
                InfrastructureTransactionLogJpaEntity.class
            );
            query.setParameter("paymentId", paymentId);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding transaction logs for payment ID {}: {}", paymentId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding TransactionLogs by Payment ID", e);
        }
    }

    @Override
    public List<DomainTransactionLogEntity> findLogsByStatusCodeAndDateRange(
            Integer statusCode,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        try {
            TypedQuery<InfrastructureTransactionLogJpaEntity> query = entityManager.createQuery(
                "SELECT t FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.statusCode = :statusCode " +
                "AND t.timestamp BETWEEN :startDate AND :endDate " +
                "ORDER BY t.timestamp DESC",
                InfrastructureTransactionLogJpaEntity.class
            );
            query.setParameter("statusCode", statusCode);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding logs by status code {} and date range: {}", statusCode, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding logs by status code and date range", e);
        }
    }

    @Override
    public Optional<DomainTransactionLogEntity> findLogById(UUID logId) {
        try {
            InfrastructureTransactionLogJpaEntity entity = entityManager.find(InfrastructureTransactionLogJpaEntity.class, logId);
            return Optional.ofNullable(mapToDomainEntity(entity));
        } catch (Exception e) {
            log.error("Error finding log by ID {}: {}", logId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding log by ID", e);
        }
    }

    @Override
    @Transactional
    public long archiveOldLogs(LocalDateTime beforeDate) {
        try {
            return entityManager.createQuery(
                "UPDATE InfrastructureTransactionLogJpaEntity t " +
                "SET t.archived = true " +
                "WHERE t.timestamp < :beforeDate")
                .setParameter("beforeDate", beforeDate)
                .executeUpdate();
        } catch (Exception e) {
            log.error("Error archiving old logs: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error archiving old logs", e);
        }
    }

    @Override
    public List<DomainTransactionLogEntity> findErrorLogs(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            TypedQuery<InfrastructureTransactionLogJpaEntity> query = entityManager.createQuery(
                "SELECT t FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.statusCode >= 400 " +
                "AND t.timestamp BETWEEN :startDate AND :endDate " +
                "ORDER BY t.timestamp DESC",
                InfrastructureTransactionLogJpaEntity.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding error logs: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding error logs", e);
        }
    }

    @Override
    public Map<Integer, Long> aggregateStatusCodes(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> results = entityManager.createQuery(
                "SELECT t.statusCode, COUNT(t) FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.timestamp BETWEEN :startDate AND :endDate " +
                "GROUP BY t.statusCode",
                Object[].class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

            Map<Integer, Long> aggregation = new HashMap<>();
            for (Object[] result : results) {
                aggregation.put((Integer) result[0], (Long) result[1]);
            }
            return aggregation;
        } catch (Exception e) {
            log.error("Error aggregating status codes: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error aggregating status codes", e);
        }
    }

    @Override
    public List<DomainTransactionLogEntity> findLogsByGatewayResponsePattern(
            String pattern,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        try {
            TypedQuery<InfrastructureTransactionLogJpaEntity> query = entityManager.createQuery(
                "SELECT t FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.gatewayResponse LIKE :pattern " +
                "AND t.timestamp BETWEEN :startDate AND :endDate " +
                "ORDER BY t.timestamp DESC",
                InfrastructureTransactionLogJpaEntity.class
            );
            query.setParameter("pattern", "%" + pattern + "%");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding logs by gateway response pattern: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding logs by gateway response pattern", e);
        }
    }

    @Override
    public List<DomainTransactionLogEntity> findFailurePatterns(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int minFailureCount) {
        try {
            TypedQuery<InfrastructureTransactionLogJpaEntity> query = entityManager.createQuery(
                "SELECT t FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.paymentId IN (" +
                "  SELECT t2.paymentId FROM InfrastructureTransactionLogJpaEntity t2 " +
                "  WHERE t2.statusCode >= 400 " +
                "  AND t2.timestamp BETWEEN :startDate AND :endDate " +
                "  GROUP BY t2.paymentId " +
                "  HAVING COUNT(t2) >= :minFailureCount" +
                ") ORDER BY t.timestamp DESC",
                InfrastructureTransactionLogJpaEntity.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("minFailureCount", minFailureCount);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding failure patterns: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding failure patterns", e);
        }
    }

    @Override
    @Transactional
    public long purgeOldLogs(LocalDateTime beforeDate) {
        try {
            return entityManager.createQuery(
                "DELETE FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.timestamp < :beforeDate")
                .setParameter("beforeDate", beforeDate)
                .executeUpdate();
        } catch (Exception e) {
            log.error("Error purging old logs: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error purging old logs", e);
        }
    }

    @Override
    @Transactional
    public List<DomainTransactionLogEntity> saveTransactionLogs(List<DomainTransactionLogEntity> logs) {
        try {
            return logs.stream()
                .map(this::saveTransactionLog)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error saving multiple transaction logs: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error saving multiple transaction logs", e);
        }
    }

    @Override
    public Map<Integer, Long> countLogsByStatusCode(UUID paymentId) {
        try {
            List<Object[]> results = entityManager.createQuery(
                "SELECT t.statusCode, COUNT(t) FROM InfrastructureTransactionLogJpaEntity t " +
                "WHERE t.paymentId = :paymentId " +
                "GROUP BY t.statusCode",
                Object[].class)
                .setParameter("paymentId", paymentId)
                .getResultList();

            Map<Integer, Long> counts = new HashMap<>();
            for (Object[] result : results) {
                counts.put((Integer) result[0], (Long) result[1]);
            }
            return counts;
        } catch (Exception e) {
            log.error("Error counting logs by status code for payment ID {}: {}", paymentId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error counting logs by status code", e);
        }
    }

    private InfrastructureTransactionLogJpaEntity mapToJpaEntity(DomainTransactionLogEntity domain) {
        if (domain == null) {
            return null;
        }
        return InfrastructureTransactionLogJpaEntity.builder()
            .logId(domain.getLogId())
            .paymentId(domain.getPaymentId())
            .gatewayResponse(domain.getGatewayResponse())
            .statusCode(domain.getStatusCode())
            .message(domain.getMessage())
            .timestamp(domain.getTimestamp())
            .build();
    }

    private DomainTransactionLogEntity mapToDomainEntity(InfrastructureTransactionLogJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        return DomainTransactionLogEntity.builder()
            .logId(jpa.getLogId())
            .paymentId(jpa.getPaymentId())
            .gatewayResponse(jpa.getGatewayResponse())
            .statusCode(jpa.getStatusCode())
            .message(jpa.getMessage())
            .timestamp(jpa.getTimestamp())
            .build();
    }
}
