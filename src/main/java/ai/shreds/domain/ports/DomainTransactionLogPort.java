package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainTransactionLogEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for transaction logging operations.
 * Implementations must ensure thread safety and data integrity.
 */
public interface DomainTransactionLogPort {

    /**
     * Saves a new transaction log entry.
     * @param log The transaction log to save
     * @return The saved log entry with generated ID and metadata
     */
    DomainTransactionLogEntity saveTransactionLog(DomainTransactionLogEntity log);

    /**
     * Retrieves all logs for a specific payment.
     * @param paymentId The payment ID to find logs for
     * @return List of transaction logs ordered by timestamp (newest first)
     */
    List<DomainTransactionLogEntity> findLogsByPaymentId(UUID paymentId);

    /**
     * Finds logs by status code within a date range.
     * @param statusCode The status code to search for
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of matching transaction logs
     */
    List<DomainTransactionLogEntity> findLogsByStatusCodeAndDateRange(
        Integer statusCode,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    /**
     * Retrieves a specific log entry.
     * @param logId The unique identifier of the log entry
     * @return Optional containing the log if found
     */
    Optional<DomainTransactionLogEntity> findLogById(UUID logId);

    /**
     * Archives logs older than the specified date.
     * @param beforeDate Logs before this date will be archived
     * @return Number of logs archived
     */
    long archiveOldLogs(LocalDateTime beforeDate);

    /**
     * Retrieves error logs for analysis.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of error logs (status code >= 400)
     */
    List<DomainTransactionLogEntity> findErrorLogs(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Aggregates status codes for reporting.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Map of status code to count
     */
    Map<Integer, Long> aggregateStatusCodes(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds logs containing specific gateway response patterns.
     * @param pattern The pattern to search for in gateway responses
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of matching logs
     */
    List<DomainTransactionLogEntity> findLogsByGatewayResponsePattern(
        String pattern,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    /**
     * Retrieves logs for failed payments that might need investigation.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @param minFailureCount Minimum number of failures to consider
     * @return List of logs for payments with multiple failures
     */
    List<DomainTransactionLogEntity> findFailurePatterns(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int minFailureCount
    );

    /**
     * Purges old logs according to retention policy.
     * @param beforeDate Logs before this date will be permanently deleted
     * @return Number of logs purged
     */
    long purgeOldLogs(LocalDateTime beforeDate);

    /**
     * Saves multiple log entries in a batch operation.
     * @param logs List of logs to save
     * @return List of saved log entries
     */
    List<DomainTransactionLogEntity> saveTransactionLogs(List<DomainTransactionLogEntity> logs);

    /**
     * Counts logs by status code for a specific payment.
     * @param paymentId The payment ID to analyze
     * @return Map of status code to count
     */
    Map<Integer, Long> countLogsByStatusCode(UUID paymentId);
}
