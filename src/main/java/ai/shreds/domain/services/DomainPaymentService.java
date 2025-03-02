package ai.shreds.domain.services;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.domain.entities.DomainTransactionLogEntity;
import ai.shreds.domain.exceptions.DomainPaymentException;
import ai.shreds.domain.ports.DomainPaymentRepositoryPort;
import ai.shreds.domain.ports.DomainTransactionLogPort;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;
import ai.shreds.shared.dtos.SharedPaymentResponseDTO;

public class DomainPaymentService {

    private final DomainPaymentRepositoryPort paymentRepository;
    private final DomainTransactionLogPort transactionLogPort;
    private final DomainValidationService validationService;
    
    // Thread safety for payment processing
    private final ConcurrentHashMap<UUID, Lock> paymentLocks = new ConcurrentHashMap<>();
    
    // Idempotency tracking
    private final ConcurrentHashMap<String, UUID> processedTransactions = new ConcurrentHashMap<>();

    public DomainPaymentService(DomainPaymentRepositoryPort paymentRepository,
                               DomainTransactionLogPort transactionLogPort,
                               DomainValidationService validationService) {
        this.paymentRepository = paymentRepository;
        this.transactionLogPort = transactionLogPort;
        this.validationService = validationService;
    }

    public DomainPaymentEntity processPayment(DomainPaymentEntity payment) {
        validationService.validatePayment(payment);

        // Get or create a lock for this payment
        Lock paymentLock = paymentLocks.computeIfAbsent(payment.getPaymentId(), k -> new ReentrantLock());
        paymentLock.lock();
        
        try {
            // Check idempotency
            String idempotencyKey = generateIdempotencyKey(payment);
            UUID existingPaymentId = processedTransactions.get(idempotencyKey);
            if (existingPaymentId != null) {
                return paymentRepository.findPaymentById(existingPaymentId)
                    .orElseThrow(() -> new DomainPaymentException("Payment not found", "PAYMENT_NOT_FOUND"));
            }

            // Initialize payment status
            if (payment.getStatus() == null) {
                payment.updateStatus(DomainPaymentStatusValue.PENDING);
            }

            // Save initial payment state
            DomainPaymentEntity savedPayment = paymentRepository.savePayment(payment);

            // Create initial transaction log
            createTransactionLog(savedPayment, "Payment processing initiated", 100);

            // Process payment (simulated gateway interaction)
            boolean isSuccessful = processPaymentWithGateway(savedPayment);

            // Update payment status based on processing result
            DomainPaymentStatusValue newStatus = isSuccessful ? 
                DomainPaymentStatusValue.SUCCESS : DomainPaymentStatusValue.FAILURE;
            
            // Validate status transition
            validationService.validateStatusTransition(savedPayment.getStatus(), newStatus);
            savedPayment.updateStatus(newStatus);

            // Save final payment state
            DomainPaymentEntity finalPayment = paymentRepository.savePayment(savedPayment);

            // Create final transaction log
            createTransactionLog(
                finalPayment,
                isSuccessful ? "Payment processed successfully" : "Payment processing failed",
                isSuccessful ? 200 : 400
            );

            // Record successful processing for idempotency
            processedTransactions.put(idempotencyKey, finalPayment.getPaymentId());

            return finalPayment;
        } finally {
            paymentLock.unlock();
            // Cleanup lock if no longer needed
            paymentLocks.remove(payment.getPaymentId());
        }
    }

    public DomainPaymentEntity updatePaymentStatus(UUID paymentId, DomainPaymentStatusValue newStatus) {
        Lock paymentLock = paymentLocks.computeIfAbsent(paymentId, k -> new ReentrantLock());
        paymentLock.lock();
        
        try {
            DomainPaymentEntity payment = paymentRepository.findPaymentById(paymentId)
                .orElseThrow(() -> new DomainPaymentException("Payment not found", "PAYMENT_NOT_FOUND"));

            // Validate status transition
            validationService.validateStatusTransition(payment.getStatus(), newStatus);
            
            // Update status
            payment.updateStatus(newStatus);
            DomainPaymentEntity updatedPayment = paymentRepository.savePayment(payment);

            // Log status update
            createTransactionLog(
                updatedPayment,
                "Payment status updated to " + newStatus,
                determineStatusCode(newStatus)
            );

            return updatedPayment;
        } finally {
            paymentLock.unlock();
            paymentLocks.remove(paymentId);
        }
    }

    public DomainPaymentEntity getPaymentDetails(UUID paymentId) {
        return paymentRepository.findPaymentById(paymentId)
            .orElseThrow(() -> new DomainPaymentException("Payment not found", "PAYMENT_NOT_FOUND"));
    }

    public SharedPaymentResponseDTO mapToSharedResponse(DomainPaymentEntity entity) {
        return SharedPaymentResponseDTO.builder()
            .paymentId(entity.getPaymentId())
            .orderId(entity.getOrderId())
            .status(entity.getStatus().name())
            .message(generateStatusMessage(entity.getStatus()))
            .build();
    }

    private DomainTransactionLogEntity createTransactionLog(DomainPaymentEntity payment, String message, Integer statusCode) {
        DomainTransactionLogEntity log = DomainTransactionLogEntity.builder()
            .paymentId(payment.getPaymentId())
            .gatewayResponse("Gateway interaction for payment " + payment.getPaymentId())
            .statusCode(statusCode)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();

        validationService.validateTransactionLog(log);
        return transactionLogPort.saveTransactionLog(log);
    }

    private boolean processPaymentWithGateway(DomainPaymentEntity payment) {
        // Simulate gateway processing
        // In real implementation, this would interact with a payment gateway
        return payment.getAmount().getAmount().compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    private String generateIdempotencyKey(DomainPaymentEntity payment) {
        return payment.getOrderId() + "-" + 
               payment.getAmount().getAmount() + "-" + 
               payment.getPaymentMethod() + "-" + 
               payment.getCreatedAt();
    }

    private int determineStatusCode(DomainPaymentStatusValue status) {
        switch (status) {
            case SUCCESS:
                return 200;
            case FAILURE:
                return 400;
            case PENDING:
                return 102;
            default:
                return 500;
        }
    }

    private String generateStatusMessage(DomainPaymentStatusValue status) {
        switch (status) {
            case SUCCESS:
                return "Payment processed successfully";
            case FAILURE:
                return "Payment processing failed";
            case PENDING:
                return "Payment is being processed";
            default:
                return "Unknown payment status";
        }
    }
}
