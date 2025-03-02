package ai.shreds.infrastructure.exceptions;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class InfrastructureNotificationException extends RuntimeException {

    private final String errorCode;
    private final String notificationType;
    private final String destination;
    private final Integer retryCount;
    private final String messageId;
    private final LocalDateTime timestamp;
    private final String correlationId;

    public InfrastructureNotificationException(String message, String errorCode, 
            String notificationType, String destination, Integer retryCount,
            String messageId, String correlationId) {
        super(message);
        this.errorCode = errorCode;
        this.notificationType = notificationType;
        this.destination = destination;
        this.retryCount = retryCount;
        this.messageId = messageId;
        this.timestamp = LocalDateTime.now();
        this.correlationId = correlationId;
    }

    public InfrastructureNotificationException(String message, Throwable cause, String errorCode, 
            String notificationType, String destination, Integer retryCount,
            String messageId, String correlationId) {
        super(message, cause);
        this.errorCode = errorCode;
        this.notificationType = notificationType;
        this.destination = destination;
        this.retryCount = retryCount;
        this.messageId = messageId;
        this.timestamp = LocalDateTime.now();
        this.correlationId = correlationId;
    }

    public static InfrastructureNotificationException deliveryError(String notificationType, 
            String destination, Integer retryCount, String messageId, String correlationId, 
            Throwable cause) {
        return new InfrastructureNotificationException(
            String.format("Failed to deliver %s notification to %s after %d attempts", 
                notificationType, destination, retryCount),
            cause,
            "NOTIFICATION_DELIVERY_ERROR",
            notificationType,
            destination,
            retryCount,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException serializationError(String notificationType, 
            String messageId, String correlationId, Throwable cause) {
        return new InfrastructureNotificationException(
            String.format("Failed to serialize %s notification", notificationType),
            cause,
            "NOTIFICATION_SERIALIZATION_ERROR",
            notificationType,
            null,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException topicError(String notificationType, 
            String destination, String messageId, String correlationId) {
        return new InfrastructureNotificationException(
            String.format("Failed to publish %s notification to topic %s", 
                notificationType, destination),
            "NOTIFICATION_TOPIC_ERROR",
            notificationType,
            destination,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException deadLetterError(String notificationType, 
            String originalDestination, String messageId, String correlationId, Throwable cause) {
        return new InfrastructureNotificationException(
            String.format("Failed to move %s notification to dead letter queue from %s", 
                notificationType, originalDestination),
            cause,
            "NOTIFICATION_DLQ_ERROR",
            notificationType,
            originalDestination,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException configurationError(String notificationType, 
            String details, String correlationId) {
        return new InfrastructureNotificationException(
            String.format("Configuration error for %s notification: %s", 
                notificationType, details),
            "NOTIFICATION_CONFIG_ERROR",
            notificationType,
            null,
            0,
            null,
            correlationId
        );
    }

    public static InfrastructureNotificationException partitionAssignmentError(String notificationType, 
            String destination, String messageId, String correlationId) {
        return new InfrastructureNotificationException(
            String.format("Failed to assign partition for %s notification in topic %s", 
                notificationType, destination),
            "NOTIFICATION_PARTITION_ERROR",
            notificationType,
            destination,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException messageValidationError(String notificationType, 
            String messageId, String correlationId, String details) {
        return new InfrastructureNotificationException(
            String.format("Message validation failed for %s notification: %s", 
                notificationType, details),
            "NOTIFICATION_VALIDATION_ERROR",
            notificationType,
            null,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException rateLimitError(String notificationType, 
            String destination, String messageId, String correlationId) {
        return new InfrastructureNotificationException(
            String.format("Rate limit exceeded for %s notification to %s", 
                notificationType, destination),
            "NOTIFICATION_RATE_LIMIT_ERROR",
            notificationType,
            destination,
            0,
            messageId,
            correlationId
        );
    }

    public static InfrastructureNotificationException acknowledgmentError(String notificationType, 
            String destination, String messageId, String correlationId, Integer retryCount) {
        return new InfrastructureNotificationException(
            String.format("Failed to receive acknowledgment for %s notification to %s after %d attempts", 
                notificationType, destination, retryCount),
            "NOTIFICATION_ACK_ERROR",
            notificationType,
            destination,
            retryCount,
            messageId,
            correlationId
        );
    }
}
