package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for payment persistence operations.
 * Implementations should ensure thread safety and transactional integrity.
 */
public interface DomainPaymentRepositoryPort {

    /**
     * Saves or updates a payment entity.
     * @param payment The payment entity to save
     * @return The saved payment entity with updated metadata
     * @throws DomainPaymentException if save operation fails
     */
    DomainPaymentEntity savePayment(DomainPaymentEntity payment);

    /**
     * Retrieves a payment by its ID.
     * @param paymentId The unique identifier of the payment
     * @return Optional containing the payment if found
     */
    Optional<DomainPaymentEntity> findPaymentById(UUID paymentId);

    /**
     * Finds payments by their status.
     * @param status The payment status to search for
     * @return List of payments with the specified status
     */
    List<DomainPaymentEntity> findPaymentsByStatus(DomainPaymentStatusValue status);

    /**
     * Finds payments for a specific order.
     * @param orderId The order ID to search for
     * @return List of payments associated with the order
     */
    List<DomainPaymentEntity> findPaymentsByOrderId(UUID orderId);

    /**
     * Finds payments within a date range.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of payments within the date range
     */
    List<DomainPaymentEntity> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Saves multiple payments in a batch operation.
     * @param payments List of payments to save
     * @return List of saved payments
     */
    List<DomainPaymentEntity> savePayments(List<DomainPaymentEntity> payments);

    /**
     * Checks if a payment exists with the given ID.
     * @param paymentId The payment ID to check
     * @return true if payment exists, false otherwise
     */
    boolean existsById(UUID paymentId);

    /**
     * Attempts to acquire a lock on a payment for processing.
     * @param paymentId The payment ID to lock
     * @return true if lock was acquired, false otherwise
     */
    boolean tryLock(UUID paymentId);

    /**
     * Releases a previously acquired lock.
     * @param paymentId The payment ID to unlock
     */
    void releaseLock(UUID paymentId);

    /**
     * Counts payments by status.
     * @param status The payment status to count
     * @return Number of payments with the specified status
     */
    long countByStatus(DomainPaymentStatusValue status);

    /**
     * Archives old payments based on date.
     * @param beforeDate Payments before this date will be archived
     * @return Number of payments archived
     */
    long archivePayments(LocalDateTime beforeDate);

    /**
     * Finds payments requiring retry processing.
     * @param maxAttempts Maximum number of retry attempts
     * @param beforeDate Consider only payments before this date
     * @return List of payments eligible for retry
     */
    List<DomainPaymentEntity> findPaymentsForRetry(int maxAttempts, LocalDateTime beforeDate);
}
