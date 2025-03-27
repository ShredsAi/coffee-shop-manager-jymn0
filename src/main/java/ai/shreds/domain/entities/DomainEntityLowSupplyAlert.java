package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainInventoryException;
import ai.shreds.shared.dtos.SharedLowSupplyAlertDTO;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DomainEntityLowSupplyAlert {

    private final UUID alertId;
    private final UUID itemId;
    private final Instant alertDate;
    private final String alertMessage;

    private DomainEntityLowSupplyAlert(Builder builder) {
        validateBuilderInputs(builder);
        this.alertId = builder.alertId;
        this.itemId = builder.itemId;
        this.alertDate = builder.alertDate;
        this.alertMessage = builder.alertMessage;
    }

    private void validateBuilderInputs(Builder builder) {
        if (builder.alertId == null) {
            throw new DomainInventoryException("Alert ID cannot be null");
        }
        if (builder.itemId == null) {
            throw new DomainInventoryException("Item ID cannot be null");
        }
        if (builder.alertDate == null) {
            throw new DomainInventoryException("Alert date cannot be null");
        }
        if (builder.alertMessage == null || builder.alertMessage.trim().isEmpty()) {
            throw new DomainInventoryException("Alert message cannot be null or empty");
        }
        if (builder.alertDate.isAfter(Instant.now())) {
            throw new DomainInventoryException("Alert date cannot be in the future");
        }
    }

    public SharedLowSupplyAlertDTO toSharedDTO() {
        return new SharedLowSupplyAlertDTO(
                this.alertId,
                this.itemId,
                Timestamp.from(this.alertDate),
                this.alertMessage
        );
    }

    public UUID getAlertId() {
        return alertId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public Instant getAlertDate() {
        return alertDate;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntityLowSupplyAlert that = (DomainEntityLowSupplyAlert) o;
        return Objects.equals(alertId, that.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DomainEntityLowSupplyAlert{");
        sb.append("alertId=").append(alertId);
        sb.append(", itemId=").append(itemId);
        sb.append(", alertDate=").append(alertDate);
        sb.append(", alertMessage='").append(alertMessage).append("'");
        sb.append("}");
        return sb.toString();
    }

    public static class Builder {
        private UUID alertId;
        private UUID itemId;
        private Instant alertDate;
        private String alertMessage;

        public Builder alertId(UUID alertId) {
            this.alertId = alertId;
            return this;
        }

        public Builder itemId(UUID itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder alertDate(Instant alertDate) {
            this.alertDate = alertDate;
            return this;
        }

        public Builder alertMessage(String alertMessage) {
            this.alertMessage = alertMessage;
            return this;
        }

        public DomainEntityLowSupplyAlert build() {
            return new DomainEntityLowSupplyAlert(this);
        }
    }
}
