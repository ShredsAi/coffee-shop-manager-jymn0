package ai.shreds.infrastructure.entities;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "low_supply_alerts",
    indexes = {
        @Index(name = "idx_alert_item_id", columnList = "item_id"),
        @Index(name = "idx_alert_date", columnList = "alert_date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureLowSupplyAlertJpaEntity {

    @Id
    @Column(name = "alert_id", nullable = false, updatable = false)
    private UUID alertId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "alert_date", nullable = false)
    private Timestamp alertDate;

    @Column(name = "alert_message", nullable = false, length = 255)
    private String alertMessage;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (alertId == null) {
            alertId = UUID.randomUUID();
        }
        if (alertDate == null) {
            alertDate = new Timestamp(System.currentTimeMillis());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts this JPA entity to a DomainEntityLowSupplyAlert.
     * @return DomainEntityLowSupplyAlert
     */
    public DomainEntityLowSupplyAlert toDomainEntity() {
        return new DomainEntityLowSupplyAlert.Builder()
            .alertId(this.alertId)
            .itemId(this.itemId)
            .alertDate(this.alertDate.toInstant())
            .alertMessage(this.alertMessage)
            .build();
    }

    /**
     * Creates a JPA entity from a domain entity.
     * @param domainEntity the domain entity to convert
     * @return InfrastructureLowSupplyAlertJpaEntity instance
     */
    public static InfrastructureLowSupplyAlertJpaEntity fromDomainEntity(DomainEntityLowSupplyAlert domainEntity) {
        return builder()
            .alertId(domainEntity.getAlertId())
            .itemId(domainEntity.getItemId())
            .alertDate(Timestamp.from(domainEntity.getAlertDate()))
            .alertMessage(domainEntity.getAlertMessage())
            .build();
    }

    public static class Builder {
        private final InfrastructureLowSupplyAlertJpaEntity instance = new InfrastructureLowSupplyAlertJpaEntity();

        public Builder alertId(UUID alertId) {
            instance.setAlertId(alertId);
            return this;
        }

        public Builder itemId(UUID itemId) {
            instance.setItemId(itemId);
            return this;
        }

        public Builder alertDate(Timestamp alertDate) {
            instance.setAlertDate(alertDate);
            return this;
        }

        public Builder alertMessage(String alertMessage) {
            instance.setAlertMessage(alertMessage);
            return this;
        }

        public Builder version(Long version) {
            instance.setVersion(version);
            return this;
        }

        public InfrastructureLowSupplyAlertJpaEntity build() {
            return instance;
        }
    }
}
