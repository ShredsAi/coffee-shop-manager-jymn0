package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationPaymentInputPort;
import ai.shreds.application.ports.ApplicationPaymentOutputPort;
import ai.shreds.shared.dtos.SharedPaymentRequestDTO;
import ai.shreds.shared.dtos.SharedPaymentResponseDTO;
import ai.shreds.domain.services.DomainPaymentService;
import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.domain.value_objects.DomainPaymentStatusValue;
import ai.shreds.domain.value_objects.DomainMoneyValue;
import ai.shreds.application.exceptions.ApplicationPaymentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplicationPaymentService implements ApplicationPaymentInputPort {

    private final ApplicationPaymentOutputPort paymentOutputPort;
    private final DomainPaymentService domainPaymentService;

    @Autowired
    public ApplicationPaymentService(ApplicationPaymentOutputPort paymentOutputPort,
                                    DomainPaymentService domainPaymentService) {
        this.paymentOutputPort = paymentOutputPort;
        this.domainPaymentService = domainPaymentService;
    }

    @Override
    @Transactional
    public SharedPaymentResponseDTO createPayment(SharedPaymentRequestDTO request) {
        try {
            log.info("Creating payment for order: {}", request.getOrderId());
            
            // 1. Validate the request
            validatePaymentRequest(request);
            
            // 2. Create domain payment entity
            DomainPaymentEntity paymentEntity = createDomainPaymentEntity(request);
            
            // 3. Process payment through domain service
            DomainPaymentEntity processedPayment = domainPaymentService.processPayment(paymentEntity);
            
            // 4. Notify external services based on payment outcome
            notifyExternalServices(processedPayment);
            
            // 5. Create and return response
            SharedPaymentResponseDTO response = processedPayment.toSharedPaymentResponse();
            log.info("Payment processed successfully for order: {}, payment ID: {}", 
                     request.getOrderId(), response.getPaymentId());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", request.getOrderId(), e);
            throw new ApplicationPaymentException("Payment processing failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SharedPaymentResponseDTO getPaymentStatus(UUID paymentId) {
        try {
            log.info("Retrieving payment status for payment ID: {}", paymentId);
            
            DomainPaymentEntity paymentEntity = domainPaymentService.getPaymentDetails(paymentId);
            SharedPaymentResponseDTO response = paymentEntity.toSharedPaymentResponse();
            
            log.info("Retrieved payment status: {} for payment ID: {}", 
                     response.getStatus(), paymentId);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error retrieving payment status for payment ID: {}", paymentId, e);
            throw new ApplicationPaymentException("Failed to retrieve payment status", e);
        }
    }

    private void validatePaymentRequest(SharedPaymentRequestDTO request) {
        if (request == null) {
            throw new ApplicationPaymentException("Payment request cannot be null");
        }
        if (request.getOrderId() == null) {
            throw new ApplicationPaymentException("Order ID cannot be null");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ApplicationPaymentException("Payment amount must be greater than zero");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new ApplicationPaymentException("Payment method cannot be empty");
        }
    }

    private DomainPaymentEntity createDomainPaymentEntity(SharedPaymentRequestDTO request) {
        DomainPaymentEntity entity = new DomainPaymentEntity();
        entity.setPaymentId(UUID.randomUUID());
        entity.setOrderId(request.getOrderId());
        entity.setAmount(new DomainMoneyValue(request.getAmount(), "USD")); // Default currency, could be configurable
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setStatus(DomainPaymentStatusValue.PENDING);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        return entity;
    }

    private void notifyExternalServices(DomainPaymentEntity payment) {
        try {
            // Notify Order Service
            paymentOutputPort.notifyOrderService(payment.getPaymentId(), 
                                               payment.getStatus().toString());
            
            // Notify Financial Service
            paymentOutputPort.notifyFinancialService(payment.getPaymentId(), 
                                                    payment.getAmount().getAmount());
            
        } catch (Exception e) {
            log.error("Error notifying external services for payment ID: {}", 
                      payment.getPaymentId(), e);
            // We might want to implement a retry mechanism here
            throw new ApplicationPaymentException("Failed to notify external services", e);
        }
    }
}
