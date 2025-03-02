package ai.shreds.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class InfrastructureGatewayException extends RuntimeException {

    private final String errorCode;
    private final String gatewayName;
    private final String operation;
    private final Integer statusCode;
    private final String transactionId;
    private final String gatewayReference;

    public InfrastructureGatewayException(String message, String errorCode, String gatewayName, 
            String operation, Integer statusCode, String transactionId, String gatewayReference) {
        super(message);
        this.errorCode = errorCode;
        this.gatewayName = gatewayName;
        this.operation = operation;
        this.statusCode = statusCode;
        this.transactionId = transactionId;
        this.gatewayReference = gatewayReference;
    }

    public InfrastructureGatewayException(String message, Throwable cause, String errorCode, 
            String gatewayName, String operation, Integer statusCode, String transactionId, 
            String gatewayReference) {
        super(message, cause);
        this.errorCode = errorCode;
        this.gatewayName = gatewayName;
        this.operation = operation;
        this.statusCode = statusCode;
        this.transactionId = transactionId;
        this.gatewayReference = gatewayReference;
    }

    public static InfrastructureGatewayException connectionError(String gatewayName, Throwable cause) {
        return new InfrastructureGatewayException(
            String.format("Failed to connect to payment gateway: %s", gatewayName),
            cause,
            "GATEWAY_CONNECTION_ERROR",
            gatewayName,
            "CONNECT",
            503,
            null,
            null
        );
    }

    public static InfrastructureGatewayException processingError(String gatewayName, String operation, 
            Integer statusCode, String message, String transactionId, String gatewayReference) {
        return new InfrastructureGatewayException(
            message,
            "GATEWAY_PROCESSING_ERROR",
            gatewayName,
            operation,
            statusCode,
            transactionId,
            gatewayReference
        );
    }

    public static InfrastructureGatewayException timeoutError(String gatewayName, String operation, 
            String transactionId) {
        return new InfrastructureGatewayException(
            String.format("Operation %s timed out for gateway %s", operation, gatewayName),
            "GATEWAY_TIMEOUT",
            gatewayName,
            operation,
            504,
            transactionId,
            null
        );
    }

    public static InfrastructureGatewayException authenticationError(String gatewayName) {
        return new InfrastructureGatewayException(
            String.format("Authentication failed for gateway %s", gatewayName),
            "GATEWAY_AUTH_ERROR",
            gatewayName,
            "AUTHENTICATE",
            401,
            null,
            null
        );
    }

    public static InfrastructureGatewayException validationError(String gatewayName, String message) {
        return new InfrastructureGatewayException(
            message,
            "GATEWAY_VALIDATION_ERROR",
            gatewayName,
            "VALIDATE",
            400,
            null,
            null
        );
    }

    public static InfrastructureGatewayException duplicateTransactionError(String gatewayName, 
            String transactionId, String gatewayReference) {
        return new InfrastructureGatewayException(
            String.format("Duplicate transaction detected for gateway %s", gatewayName),
            "GATEWAY_DUPLICATE_ERROR",
            gatewayName,
            "PROCESS_PAYMENT",
            409,
            transactionId,
            gatewayReference
        );
    }

    public static InfrastructureGatewayException fraudDetectionError(String gatewayName, 
            String transactionId, String message) {
        return new InfrastructureGatewayException(
            message,
            "GATEWAY_FRAUD_ERROR",
            gatewayName,
            "FRAUD_CHECK",
            403,
            transactionId,
            null
        );
    }

    public static InfrastructureGatewayException reconciliationError(String gatewayName, 
            String transactionId, String gatewayReference) {
        return new InfrastructureGatewayException(
            String.format("Failed to reconcile transaction %s with gateway %s", transactionId, gatewayName),
            "GATEWAY_RECONCILIATION_ERROR",
            gatewayName,
            "RECONCILE",
            500,
            transactionId,
            gatewayReference
        );
    }

    public static InfrastructureGatewayException refundError(String gatewayName, String transactionId, 
            String gatewayReference, String message) {
        return new InfrastructureGatewayException(
            message,
            "GATEWAY_REFUND_ERROR",
            gatewayName,
            "REFUND",
            400,
            transactionId,
            gatewayReference
        );
    }
}
