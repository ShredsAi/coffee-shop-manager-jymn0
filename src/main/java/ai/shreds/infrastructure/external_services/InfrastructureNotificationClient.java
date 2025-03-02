package ai.shreds.infrastructure.external_services;

import ai.shreds.infrastructure.external_services.model.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class InfrastructureNotificationClient {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String orderNotificationTopic;
    private final String financialNotificationTopic;
    private final String deadLetterTopic;

    public InfrastructureNotificationClient(
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${payment.kafka.order-notification-topic}") String orderNotificationTopic,
            @Value("${payment.kafka.financial-notification-topic}") String financialNotificationTopic,
            @Value("${payment.kafka.dead-letter-topic}") String deadLetterTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.orderNotificationTopic = orderNotificationTopic;
        this.financialNotificationTopic = financialNotificationTopic;
        this.deadLetterTopic = deadLetterTopic;
    }

    @Retry(name = "kafkaNotification")
    public void notifyOrderService(UUID paymentId, UUID orderId, String status, BigDecimal amount, 
            String currency, String paymentMethod) {
        try {
            log.info("Sending order notification for payment: {}, order: {}, status: {}", 
                    paymentId, orderId, status);

            NotificationMessage notification = NotificationMessage.createOrderNotification(
                paymentId, orderId, status, amount, currency, paymentMethod);

            Message<NotificationMessage> message = MessageBuilder
                .withPayload(notification)
                .setHeader(KafkaHeaders.TOPIC, orderNotificationTopic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, orderId.toString())
                .setHeader("notification-type", "ORDER_NOTIFICATION")
                .build();

            sendMessageWithCallback(message, orderNotificationTopic);
        } catch (Exception e) {
            log.error("Error sending order notification for payment {}: {}", paymentId, e.getMessage(), e);
            handleNotificationError(paymentId, orderId, "ORDER_NOTIFICATION", e);
        }
    }

    @Retry(name = "kafkaNotification")
    public void notifyFinancialService(UUID paymentId, UUID orderId, BigDecimal amount, 
            String currency, String paymentMethod) {
        try {
            log.info("Sending financial notification for payment: {}, amount: {}", paymentId, amount);

            NotificationMessage notification = NotificationMessage.createFinancialNotification(
                paymentId, orderId, amount, currency, paymentMethod);

            Message<NotificationMessage> message = MessageBuilder
                .withPayload(notification)
                .setHeader(KafkaHeaders.TOPIC, financialNotificationTopic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, paymentId.toString())
                .setHeader("notification-type", "FINANCIAL_NOTIFICATION")
                .build();

            sendMessageWithCallback(message, financialNotificationTopic);
        } catch (Exception e) {
            log.error("Error sending financial notification for payment {}: {}", paymentId, e.getMessage(), e);
            handleNotificationError(paymentId, orderId, "FINANCIAL_NOTIFICATION", e);
        }
    }

    private void sendMessageWithCallback(Message<?> message, String topic) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.debug("Successfully sent message to topic {}: {}", topic, result.getProducerRecord().value());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Failed to send message to topic {}: {}", topic, ex.getMessage(), ex);
                handleKafkaError(message, topic, ex);
            }
        });
    }

    private void handleNotificationError(UUID paymentId, UUID orderId, String notificationType, Exception e) {
        NotificationMessage errorNotification = NotificationMessage.builder()
            .notificationId(UUID.randomUUID())
            .type(notificationType)
            .paymentId(paymentId)
            .orderId(orderId)
            .errorCode("NOTIFICATION_ERROR")
            .errorMessage(e.getMessage())
            .retryCount(0)
            .build();

        Message<NotificationMessage> message = MessageBuilder
            .withPayload(errorNotification)
            .setHeader(KafkaHeaders.TOPIC, deadLetterTopic)
            .setHeader(KafkaHeaders.MESSAGE_KEY, paymentId.toString())
            .setHeader("error-type", "NOTIFICATION_ERROR")
            .build();

        kafkaTemplate.send(message);
    }

    private void handleKafkaError(Message<?> originalMessage, String originalTopic, Throwable ex) {
        try {
            Message<?> deadLetterMessage = MessageBuilder
                .withPayload(originalMessage.getPayload())
                .setHeader(KafkaHeaders.TOPIC, deadLetterTopic)
                .setHeader("original-topic", originalTopic)
                .setHeader("error-message", ex.getMessage())
                .setHeader("timestamp", System.currentTimeMillis())
                .build();

            kafkaTemplate.send(deadLetterMessage);
        } catch (Exception e) {
            log.error("Failed to send message to dead letter topic: {}", e.getMessage(), e);
        }
    }
}
