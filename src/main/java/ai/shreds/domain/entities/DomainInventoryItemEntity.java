package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainQuantityValue;
import ai.shreds.domain.value_objects.DomainThresholdValue;
import java.sql.Timestamp;
import java.util.UUID;

public class DomainInventoryItemEntity {
    private UUID itemId;
    private String itemName;
    private DomainQuantityValue quantity;
    private DomainThresholdValue threshold;
    private Timestamp updatedAt;

    public DomainInventoryItemEntity(UUID itemId, String itemName, DomainQuantityValue quantity, 
            DomainThresholdValue threshold, Timestamp updatedAt) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.threshold = threshold;
        this.updatedAt = updatedAt;
    }

    public UUID getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public DomainQuantityValue getQuantity() {
        return quantity;
    }

    public DomainThresholdValue getThreshold() {
        return threshold;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void updateQuantity(DomainQuantityValue newQuantity) {
        this.quantity = newQuantity;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public boolean isBelowThreshold() {
        return quantity.getValue() < threshold.getValue();
    }
}