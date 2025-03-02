package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainPaymentEntity;
import ai.shreds.infrastructure.external_services.model.GatewayRequest;
import ai.shreds.infrastructure.external_services.model.GatewayResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class InfrastructurePaymentGatewayClient {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;
    private final String apiKey;
    private final String merchantId;
    private final String webhookSecret;

    public InfrastructurePaymentGatewayClient(
            RestTemplate restTemplate,
            @Value("${payment.gateway.url}") String gatewayUrl,
            @Value("${payment.gateway.api-key}") String apiKey,
            @Value("${payment.gateway.merchant-id}") String merchantId,
            @Value("${payment.gateway.webhook-secret}") String webhookSecret) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
        this.apiKey = apiKey;
        this.merchantId = merchantId;
        this.webhookSecret = webhookSecret;
    }

    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "processPaymentFallback")
    @Retry(name = "paymentGateway")
    public GatewayResponse processPayment(@Valid DomainPaymentEntity payment) {
        log.info("Processing payment with ID: {} for order: {}", payment.getPaymentId(), payment.getOrderId());
        long startTime = System.currentTimeMillis();

        try {
            GatewayRequest request = buildGatewayRequest(payment);
            HttpEntity<GatewayRequest> httpEntity = new HttpEntity<>(request, createHeaders());

            ResponseEntity<GatewayResponse> response = restTemplate.exchange(
                gatewayUrl + "/v1/payments",
                HttpMethod.POST,
                httpEntity,
                GatewayResponse.class
            );

            GatewayResponse gatewayResponse = response.getBody();
            if (gatewayResponse != null) {
                gatewayResponse.setProcessingTime(LocalDateTime.now());
                gatewayResponse.setProcessingDurationMs(System.currentTimeMillis() - startTime);
                gatewayResponse.setStatusCode(response.getStatusCodeValue());
            }

            log.info("Payment processed successfully for ID: {}", payment.getPaymentId());
            return gatewayResponse;

        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            return buildErrorResponse(e, startTime);
        }
    }

    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "validatePaymentMethodFallback")
    @Retry(name = "paymentGateway")
    public boolean validatePaymentMethod(String method) {
        log.debug("Validating payment method: {}", method);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                gatewayUrl + "/v1/payment-methods/" + method + "/validate",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Error validating payment method {}: {}", method, e.getMessage(), e);
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("X-Merchant-ID", merchantId);
        headers.set("X-Request-ID", UUID.randomUUID().toString());
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private GatewayRequest buildGatewayRequest(DomainPaymentEntity payment) {
        return GatewayRequest.builder()
            .paymentId(payment.getPaymentId())
            .orderId(payment.getOrderId())
            .amount(payment.getAmount().getAmount())
            .currency(payment.getAmount().getCurrency())
            .paymentMethod(payment.getPaymentMethod())
            .description("Payment for order " + payment.getOrderId())
            .metadata(buildMetadata(payment))
            .build();
    }

    private Map<String, String> buildMetadata(DomainPaymentEntity payment) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("orderId", payment.getOrderId().toString());
        metadata.put("paymentId", payment.getPaymentId().toString());
        metadata.put("createdAt", payment.getCreatedAt().toString());
        return metadata;
    }

    private GatewayResponse buildErrorResponse(Exception e, long startTime) {
        return GatewayResponse.builder()
            .success(false)
            .message("Payment processing failed")
            .errorCode("PROCESSING_ERROR")
            .errorDetails(e.getMessage())
            .processingTime(LocalDateTime.now())
            .processingDurationMs(System.currentTimeMillis() - startTime)
            .statusCode(500)
            .build();
    }

    private GatewayResponse processPaymentFallback(DomainPaymentEntity payment, Exception e) {
        log.error("Circuit breaker fallback: Error processing payment: {}", e.getMessage(), e);
        return GatewayResponse.builder()
            .success(false)
            .message("Payment service temporarily unavailable")
            .errorCode("SERVICE_UNAVAILABLE")
            .errorDetails("Payment gateway is currently unavailable. Please try again later.")
            .processingTime(LocalDateTime.now())
            .statusCode(503)
            .build();
    }

    private boolean validatePaymentMethodFallback(String method, Exception e) {
        log.error("Circuit breaker fallback: Error validating payment method: {}", e.getMessage(), e);
        return false;
    }
}
