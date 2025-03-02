package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.domain.ports.DomainPaymentRepositoryPort;
import ai.shreds.domain.value_objects.DomainMoneyValue;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional(readOnly = true)
public class InfrastructurePaymentRepositoryImpl implements DomainPaymentRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DomainPaymentEntity savePayment(@NotNull @Valid DomainPaymentEntity payment) {
        try {
            log.debug("Saving payment with ID: {}", payment.getPaymentId());
            InfrastructurePaymentJpaEntity jpaEntity = mapToJpaEntity(payment);
            
            if (jpaEntity.getPaymentId() == null) {
                entityManager.persist(jpaEntity);
            } else {
                jpaEntity = entityManager.merge(jpaEntity);
            }
            
            entityManager.flush();
            log.info("Successfully saved payment with ID: {}", jpaEntity.getPaymentId());
            return mapToDomainEntity(jpaEntity);
        } catch (Exception e) {
            log.error("Error saving payment: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error saving Payment", e);
        }
    }

    @Override
    public Optional<DomainPaymentEntity> findPaymentById(@NotNull UUID paymentId) {
        try {
            log.debug("Finding payment by ID: {}", paymentId);
            InfrastructurePaymentJpaEntity jpaEntity = entityManager.find(InfrastructurePaymentJpaEntity.class, paymentId);
            return Optional.ofNullable(mapToDomainEntity(jpaEntity));
        } catch (Exception e) {
            log.error("Error finding payment by ID {}: {}", paymentId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding Payment by ID", e);
        }
    }

    @Override
    public List<DomainPaymentEntity> findPaymentsByStatus(DomainPaymentStatusValue status) {
        try {
            TypedQuery<InfrastructurePaymentJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM InfrastructurePaymentJpaEntity p WHERE p.status = :status",
                InfrastructurePaymentJpaEntity.class
            );
            query.setParameter("status", status.name());
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding payments by status {}: {}", status, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding Payments by status", e);
        }
    }

    @Override
    public List<DomainPaymentEntity> findPaymentsByOrderId(@NotNull UUID orderId) {
        try {
            TypedQuery<InfrastructurePaymentJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM InfrastructurePaymentJpaEntity p WHERE p.orderId = :orderId",
                InfrastructurePaymentJpaEntity.class
            );
            query.setParameter("orderId", orderId);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding payments by order ID {}: {}", orderId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding Payments by Order ID", e);
        }
    }

    @Override
    public List<DomainPaymentEntity> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            TypedQuery<InfrastructurePaymentJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM InfrastructurePaymentJpaEntity p WHERE p.createdAt BETWEEN :startDate AND :endDate",
                InfrastructurePaymentJpaEntity.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding payments between dates {} and {}: {}", startDate, endDate, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding Payments by date range", e);
        }
    }

    @Override
    @Transactional
    public List<DomainPaymentEntity> savePayments(List<DomainPaymentEntity> payments) {
        try {
            return payments.stream()
                .map(this::savePayment)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error saving multiple payments: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error saving multiple Payments", e);
        }
    }

    @Override
    public boolean existsById(UUID paymentId) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM InfrastructurePaymentJpaEntity p WHERE p.paymentId = :paymentId",
                Long.class
            );
            query.setParameter("paymentId", paymentId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            log.error("Error checking payment existence for ID {}: {}", paymentId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error checking Payment existence", e);
        }
    }

    @Override
    @Transactional
    public boolean tryLock(UUID paymentId) {
        try {
            entityManager.find(InfrastructurePaymentJpaEntity.class, paymentId, LockModeType.PESSIMISTIC_WRITE);
            return true;
        } catch (Exception e) {
            log.debug("Could not acquire lock for payment ID {}: {}", paymentId, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void releaseLock(UUID paymentId) {
        try {
            entityManager.lock(entityManager.getReference(InfrastructurePaymentJpaEntity.class, paymentId), 
                              LockModeType.NONE);
        } catch (Exception e) {
            log.error("Error releasing lock for payment ID {}: {}", paymentId, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error releasing Payment lock", e);
        }
    }

    @Override
    public long countByStatus(DomainPaymentStatusValue status) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM InfrastructurePaymentJpaEntity p WHERE p.status = :status",
                Long.class
            );
            query.setParameter("status", status.name());
            return query.getSingleResult();
        } catch (Exception e) {
            log.error("Error counting payments by status {}: {}", status, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error counting Payments by status", e);
        }
    }

    @Override
    @Transactional
    public long archivePayments(LocalDateTime beforeDate) {
        try {
            return entityManager.createQuery(
                "UPDATE InfrastructurePaymentJpaEntity p SET p.status = :archivedStatus " +
                "WHERE p.createdAt < :beforeDate AND p.status NOT IN (:excludedStatuses)")
                .setParameter("archivedStatus", DomainPaymentStatusValue.ARCHIVED.name())
                .setParameter("beforeDate", beforeDate)
                .setParameter("excludedStatuses", List.of(DomainPaymentStatusValue.PENDING.name(), 
                                                        DomainPaymentStatusValue.PROCESSING.name()))
                .executeUpdate();
        } catch (Exception e) {
            log.error("Error archiving payments before date {}: {}", beforeDate, e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error archiving Payments", e);
        }
    }

    @Override
    public List<DomainPaymentEntity> findPaymentsForRetry(int maxAttempts, LocalDateTime beforeDate) {
        try {
            TypedQuery<InfrastructurePaymentJpaEntity> query = entityManager.createQuery(
                "SELECT p FROM InfrastructurePaymentJpaEntity p " +
                "WHERE p.status = :failedStatus " +
                "AND p.createdAt < :beforeDate " +
                "AND p.retryCount < :maxAttempts",
                InfrastructurePaymentJpaEntity.class
            );
            query.setParameter("failedStatus", DomainPaymentStatusValue.FAILURE.name());
            query.setParameter("beforeDate", beforeDate);
            query.setParameter("maxAttempts", maxAttempts);
            return query.getResultList().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding payments for retry: {}", e.getMessage(), e);
            throw new InfrastructureRepositoryException("Error finding Payments for retry", e);
        }
    }

    private InfrastructurePaymentJpaEntity mapToJpaEntity(DomainPaymentEntity domain) {
        if (domain == null) {
            return null;
        }
        return InfrastructurePaymentJpaEntity.builder()
            .paymentId(domain.getPaymentId())
            .orderId(domain.getOrderId())
            .amount(domain.getAmount().getAmount())
            .currency(domain.getAmount().getCurrency())
            .paymentMethod(domain.getPaymentMethod())
            .status(domain.getStatus().name())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private DomainPaymentEntity mapToDomainEntity(InfrastructurePaymentJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }
        return DomainPaymentEntity.builder()
            .paymentId(jpa.getPaymentId())
            .orderId(jpa.getOrderId())
            .amount(new DomainMoneyValue(jpa.getAmount(), jpa.getCurrency()))
            .paymentMethod(jpa.getPaymentMethod())
            .status(DomainPaymentStatusValue.valueOf(jpa.getStatus()))
            .createdAt(jpa.getCreatedAt())
            .updatedAt(jpa.getUpdatedAt())
            .build();
    }
}
