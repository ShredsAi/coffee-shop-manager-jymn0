package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationPaymentOutputPort;
import ai.shreds.application.services.models.NotificationMessage;
import ai.shreds.application.exceptions.ApplicationPaymentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplicationNotificationService implements ApplicationPaymentOutputPort {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;
    
    @Value("${payment.notification.order-topic}")
    private String orderTopic;
    
    @Value("${payment.notification.financial-topic}")
    private String financialTopic;

    @Autowired
    public ApplicationNotificationService(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notifyOrderService(UUID paymentId, String status) {
        try {
            log.info("Sending payment status notification to Order Service for payment: {}, status: {}", 
                     paymentId, status);
            
            NotificationMessage message = NotificationMessage.forOrderService(paymentId, status);
            kafkaTemplate.send(orderTopic, paymentId.toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send notification to Order Service", ex);
                    } else {
                        log.debug("Notification sent to Order Service successfully. Offset: {}", 
                                 result.getRecordMetadata().offset());
                    }
                });
            
        } catch (Exception e) {
            log.error("Error notifying Order Service for payment: {}", paymentId, e);
            throw new ApplicationPaymentException("Failed to notify Order Service", e);
        }
    }

    @Override
    public void notifyFinancialService(UUID paymentId, BigDecimal amount) {
        try {
            log.info("Sending financial update notification for payment: {}, amount: {}", 
                     paymentId, amount);
            
            NotificationMessage message = NotificationMessage.forFinancialService(paymentId, amount);
            kafkaTemplate.send(financialTopic, paymentId.toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send notification to Financial Service", ex);
                    } else {
                        log.debug("Notification sent to Financial Service successfully. Offset: {}", 
                                 result.getRecordMetadata().offset());
                    }
                });
            
        } catch (Exception e) {
            log.error("Error notifying Financial Service for payment: {}", paymentId, e);
            throw new ApplicationPaymentException("Failed to notify Financial Service", e);
        }
    }
}
