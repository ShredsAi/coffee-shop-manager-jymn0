package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.domain.entities.DomainTransactionLogEntity;
import ai.shreds.domain.exceptions.DomainPaymentException;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class DomainValidationService {
    private static final Set<String> SUPPORTED_PAYMENT_METHODS = new HashSet<>(Arrays.asList(
            "credit_card",
            "debit_card",
            "mobile_wallet",
            "bank_transfer",
            "crypto"
    ));

    private static final BigDecimal MAX_PAYMENT_AMOUNT = new BigDecimal("1000000.00");
    private static final BigDecimal MIN_PAYMENT_AMOUNT = new BigDecimal("0.01");

    public void validatePayment(DomainPaymentEntity payment) {
        if (payment == null) {
            throw new DomainPaymentException("Payment is null.", "NULL_PAYMENT");
        }

        validatePaymentId(payment);
        validateOrderId(payment);
        validateAmount(payment);
        validatePaymentMethod(payment);
        validateStatus(payment);
        validateTimestamps(payment);
    }

    public void validateTransactionLog(DomainTransactionLogEntity log) {
        if (log == null) {
            throw new DomainPaymentException("Transaction log is null.", "NULL_TRANSACTION_LOG");
        }

        validateLogId(log);
        validateLogPaymentId(log);
        validateGatewayResponse(log);
        validateStatusCode(log);
        validateLogMessage(log);
        validateLogTimestamp(log);
    }

    public void validateStatusTransition(DomainPaymentStatusValue currentStatus, DomainPaymentStatusValue newStatus) {
        if (currentStatus == null) {
            throw new DomainPaymentException("Current status cannot be null", "NULL_CURRENT_STATUS");
        }
        if (newStatus == null) {
            throw new DomainPaymentException("New status cannot be null", "NULL_NEW_STATUS");
        }

        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != DomainPaymentStatusValue.SUCCESS && 
                    newStatus != DomainPaymentStatusValue.FAILURE) {
                    throw new DomainPaymentException(
                        "Invalid status transition from PENDING to " + newStatus,
                        "INVALID_STATUS_TRANSITION"
                    );
                }
                break;
            case SUCCESS:
            case FAILURE:
                throw new DomainPaymentException(
                    "Cannot transition from final status: " + currentStatus,
                    "INVALID_STATUS_TRANSITION"
                );
            default:
                throw new DomainPaymentException(
                    "Unknown current status: " + currentStatus,
                    "UNKNOWN_STATUS"
                );
        }
    }

    private void validatePaymentId(DomainPaymentEntity payment) {
        if (payment.getPaymentId() == null) {
            throw new DomainPaymentException("Payment ID is missing.", "PAYMENT_ID_MISSING");
        }
    }

    private void validateOrderId(DomainPaymentEntity payment) {
        if (payment.getOrderId() == null) {
            throw new DomainPaymentException("Order ID is missing.", "ORDER_ID_MISSING");
        }
    }

    private void validateAmount(DomainPaymentEntity payment) {
        if (payment.getAmount() == null || payment.getAmount().getAmount() == null) {
            throw new DomainPaymentException("Amount is missing.", "PAYMENT_AMOUNT_MISSING");
        }

        BigDecimal amount = payment.getAmount().getAmount();
        if (amount.compareTo(MIN_PAYMENT_AMOUNT) < 0) {
            throw new DomainPaymentException(
                "Payment amount must be at least " + MIN_PAYMENT_AMOUNT,
                "PAYMENT_AMOUNT_TOO_LOW"
            );
        }
        if (amount.compareTo(MAX_PAYMENT_AMOUNT) > 0) {
            throw new DomainPaymentException(
                "Payment amount cannot exceed " + MAX_PAYMENT_AMOUNT,
                "PAYMENT_AMOUNT_TOO_HIGH"
            );
        }
    }

    private void validatePaymentMethod(DomainPaymentEntity payment) {
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().trim().isEmpty()) {
            throw new DomainPaymentException("Payment method is missing.", "PAYMENT_METHOD_MISSING");
        }
        if (!SUPPORTED_PAYMENT_METHODS.contains(payment.getPaymentMethod().toLowerCase())) {
            throw new DomainPaymentException(
                "Unsupported payment method: " + payment.getPaymentMethod(),
                "UNSUPPORTED_PAYMENT_METHOD"
            );
        }
    }

    private void validateStatus(DomainPaymentEntity payment) {
        if (payment.getStatus() == null) {
            throw new DomainPaymentException("Payment status is missing.", "PAYMENT_STATUS_MISSING");
        }
    }

    private void validateTimestamps(DomainPaymentEntity payment) {
        LocalDateTime now = LocalDateTime.now();
        if (payment.getCreatedAt() == null) {
            throw new DomainPaymentException("Creation timestamp is missing.", "CREATED_AT_MISSING");
        }
        if (payment.getCreatedAt().isAfter(now)) {
            throw new DomainPaymentException("Creation timestamp is in the future.", "INVALID_CREATED_AT");
        }
        if (payment.getUpdatedAt() == null) {
            throw new DomainPaymentException("Update timestamp is missing.", "UPDATED_AT_MISSING");
        }
        if (payment.getUpdatedAt().isAfter(now)) {
            throw new DomainPaymentException("Update timestamp is in the future.", "INVALID_UPDATED_AT");
        }
        if (payment.getUpdatedAt().isBefore(payment.getCreatedAt())) {
            throw new DomainPaymentException(
                "Update timestamp cannot be before creation timestamp.",
                "INVALID_TIMESTAMP_ORDER"
            );
        }
    }

    private void validateLogId(DomainTransactionLogEntity log) {
        if (log.getLogId() == null) {
            throw new DomainPaymentException("Log ID is missing.", "LOG_ID_MISSING");
        }
    }

    private void validateLogPaymentId(DomainTransactionLogEntity log) {
        if (log.getPaymentId() == null) {
            throw new DomainPaymentException("Payment ID is missing in log.", "LOG_PAYMENT_ID_MISSING");
        }
    }

    private void validateGatewayResponse(DomainTransactionLogEntity log) {
        if (log.getGatewayResponse() == null) {
            throw new DomainPaymentException("Gateway response is missing.", "GATEWAY_RESPONSE_MISSING");
        }
    }

    private void validateStatusCode(DomainTransactionLogEntity log) {
        if (log.getStatusCode() == null) {
            throw new DomainPaymentException("Status code is missing.", "STATUS_CODE_MISSING");
        }
        if (log.getStatusCode() < 100 || log.getStatusCode() > 599) {
            throw new DomainPaymentException(
                "Invalid status code: " + log.getStatusCode(),
                "INVALID_STATUS_CODE"
            );
        }
    }

    private void validateLogMessage(DomainTransactionLogEntity log) {
        if (log.getMessage() == null || log.getMessage().trim().isEmpty()) {
            throw new DomainPaymentException("Log message is missing.", "LOG_MESSAGE_MISSING");
        }
    }

    private void validateLogTimestamp(DomainTransactionLogEntity log) {
        if (log.getTimestamp() == null) {
            throw new DomainPaymentException("Log timestamp is missing.", "LOG_TIMESTAMP_MISSING");
        }
        if (log.getTimestamp().isAfter(LocalDateTime.now())) {
            throw new DomainPaymentException("Log timestamp is in the future.", "INVALID_LOG_TIMESTAMP");
        }
    }
}
