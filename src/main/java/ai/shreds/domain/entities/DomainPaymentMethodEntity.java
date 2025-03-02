package ai.shreds.domain.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DomainPaymentMethodEntity {
    private static final Set<String> SUPPORTED_METHODS = new HashSet<>(Arrays.asList(
            "credit_card",
            "debit_card",
            "mobile_wallet",
            "bank_transfer",
            "crypto"
    ));

    private final String methodType;
    private final String details;

    private DomainPaymentMethodEntity(Builder builder) {
        validateMethodType(builder.methodType);
        validateDetails(builder.details);
        this.methodType = builder.methodType;
        this.details = sanitizeDetails(builder.details);
    }

    public static Builder builder() {
        return new Builder();
    }

    // Factory methods for common payment methods
    public static DomainPaymentMethodEntity createCreditCardMethod(String gatewayConfig) {
        return builder()
                .methodType("credit_card")
                .details(gatewayConfig)
                .build();
    }

    public static DomainPaymentMethodEntity createMobileWalletMethod(String providerConfig) {
        return builder()
                .methodType("mobile_wallet")
                .details(providerConfig)
                .build();
    }

    private void validateMethodType(String methodType) {
        if (methodType == null || methodType.trim().isEmpty()) {
            throw new IllegalArgumentException("Method type cannot be null or empty");
        }
        if (!SUPPORTED_METHODS.contains(methodType)) {
            throw new IllegalArgumentException("Unsupported payment method type: " + methodType);
        }
    }

    private void validateDetails(String details) {
        if (details == null || details.trim().isEmpty()) {
            throw new IllegalArgumentException("Details cannot be null or empty");
        }
        try {
            // Validate JSON structure
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(details);
            validateDetailsSchema(jsonNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON structure in details: " + e.getMessage());
        }
    }

    private void validateDetailsSchema(JsonNode jsonNode) {
        // Validate schema based on method type
        switch (methodType) {
            case "credit_card":
            case "debit_card":
                validateCardSchema(jsonNode);
                break;
            case "mobile_wallet":
                validateWalletSchema(jsonNode);
                break;
            case "bank_transfer":
                validateBankTransferSchema(jsonNode);
                break;
            case "crypto":
                validateCryptoSchema(jsonNode);
                break;
        }
    }

    private void validateCardSchema(JsonNode jsonNode) {
        if (!jsonNode.has("gateway") || !jsonNode.has("supported_networks")) {
            throw new IllegalArgumentException("Card payment method must specify gateway and supported networks");
        }
    }

    private void validateWalletSchema(JsonNode jsonNode) {
        if (!jsonNode.has("provider") || !jsonNode.has("integration_type")) {
            throw new IllegalArgumentException("Mobile wallet must specify provider and integration type");
        }
    }

    private void validateBankTransferSchema(JsonNode jsonNode) {
        if (!jsonNode.has("bank_codes") || !jsonNode.has("transfer_type")) {
            throw new IllegalArgumentException("Bank transfer must specify bank codes and transfer type");
        }
    }

    private void validateCryptoSchema(JsonNode jsonNode) {
        if (!jsonNode.has("networks") || !jsonNode.has("currencies")) {
            throw new IllegalArgumentException("Crypto payment must specify networks and supported currencies");
        }
    }

    private String sanitizeDetails(String details) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(details);
            // Remove any sensitive fields
            if (jsonNode.has("api_key")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("api_key", "[REDACTED]");
            }
            if (jsonNode.has("secret")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("secret", "[REDACTED]");
            }
            return mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sanitize details", e);
        }
    }

    public String getMethodType() {
        return methodType;
    }

    public String getDetails() {
        return details;
    }

    public boolean isCompatibleWithGateway(String gatewayProvider) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode detailsNode = mapper.readTree(details);
            return detailsNode.has("gateway") && 
                   detailsNode.get("gateway").asText().equals(gatewayProvider);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainPaymentMethodEntity that = (DomainPaymentMethodEntity) o;
        return Objects.equals(methodType, that.methodType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodType);
    }

    @Override
    public String toString() {
        return "DomainPaymentMethodEntity{" +
                "methodType='" + methodType + ''' +
                // Excluding details from toString for security
                '}';
    }

    public static class Builder {
        private String methodType;
        private String details;

        private Builder() {}

        public Builder methodType(String methodType) {
            this.methodType = methodType;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public DomainPaymentMethodEntity build() {
            return new DomainPaymentMethodEntity(this);
        }
    }
}
