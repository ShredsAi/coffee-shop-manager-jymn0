package ai.shreds.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class DomainLowSupplyAlertEntity {
    private UUID alertId;
    private UUID itemId;
    private Timestamp alertDate;
    private String alertMessage;

    public DomainLowSupplyAlertEntity(UUID alertId, UUID itemId, Timestamp alertDate, String alertMessage) {
        this.alertId = alertId;
        this.itemId = itemId;
        this.alertDate = alertDate;
        this.alertMessage = alertMessage;
    }

    public UUID getAlertId() {
        return alertId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public Timestamp getAlertDate() {
        return alertDate;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public static DomainLowSupplyAlertEntity createFor(DomainInventoryItemEntity item) {
        return new DomainLowSupplyAlertEntity(
            UUID.randomUUID(),
            item.getItemId(),
            new Timestamp(System.currentTimeMillis()),
            String.format("Low supply alert for item %s: Quantity %d is below threshold %d",
                item.getItemName(),
                item.getQuantity().getValue(),
                item.getThreshold().getValue())
        );
    }
}