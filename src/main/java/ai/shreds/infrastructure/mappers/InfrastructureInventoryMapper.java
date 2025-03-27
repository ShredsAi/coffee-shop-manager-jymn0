package ai.shreds.infrastructure.mappers;

import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.value_objects.DomainValueThreshold;
import ai.shreds.infrastructure.entities.InfrastructureInventoryItemJpaEntity;
import ai.shreds.infrastructure.entities.InfrastructureLowSupplyAlertJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring")
@Component
public interface InfrastructureInventoryMapper {

    @Mapping(source = "quantity", target = "quantity", qualifiedByName = "toQuantityValue")
    @Mapping(source = "threshold", target = "threshold", qualifiedByName = "toThresholdValue")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "timestampToInstant")
    default DomainEntityInventoryItem toDomainEntity(InfrastructureInventoryItemJpaEntity jpa) {
        if (jpa == null) return null;
        return new DomainEntityInventoryItem.Builder()
                .itemId(jpa.getItemId())
                .itemName(jpa.getItemName())
                .quantity(toQuantityValue(jpa.getQuantity()))
                .threshold(toThresholdValue(jpa.getThreshold()))
                .updatedAt(timestampToInstant(jpa.getUpdatedAt()))
                .build();
    }

    @Mapping(source = "quantity.value", target = "quantity")
    @Mapping(source = "threshold.value", target = "threshold")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToTimestamp")
    default InfrastructureInventoryItemJpaEntity toJpaEntity(DomainEntityInventoryItem domain) {
        if (domain == null) return null;
        return InfrastructureInventoryItemJpaEntity.builder()
                .itemId(domain.getItemId())
                .itemName(domain.getItemName())
                .quantity(domain.getQuantity().getValue())
                .threshold(domain.getThreshold().getValue())
                .updatedAt(instantToTimestamp(domain.getUpdatedAt()))
                .build();
    }

    @Mapping(source = "alertDate", target = "alertDate", qualifiedByName = "timestampToInstant")
    default DomainEntityLowSupplyAlert toDomainAlertEntity(InfrastructureLowSupplyAlertJpaEntity jpa) {
        if (jpa == null) return null;
        return new DomainEntityLowSupplyAlert.Builder()
                .alertId(jpa.getAlertId())
                .itemId(jpa.getItemId())
                .alertDate(timestampToInstant(jpa.getAlertDate()))
                .alertMessage(jpa.getAlertMessage())
                .build();
    }

    @Mapping(source = "alertDate", target = "alertDate", qualifiedByName = "instantToTimestamp")
    default InfrastructureLowSupplyAlertJpaEntity toJpaAlertEntity(DomainEntityLowSupplyAlert domain) {
        if (domain == null) return null;
        return InfrastructureLowSupplyAlertJpaEntity.builder()
                .alertId(domain.getAlertId())
                .itemId(domain.getItemId())
                .alertDate(instantToTimestamp(domain.getAlertDate()))
                .alertMessage(domain.getAlertMessage())
                .build();
    }

    @Named("toQuantityValue")
    default DomainValueQuantity toQuantityValue(Integer value) {
        return value == null ? null : new DomainValueQuantity(value);
    }

    @Named("toThresholdValue")
    default DomainValueThreshold toThresholdValue(Integer value) {
        return value == null ? null : new DomainValueThreshold(value);
    }

    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    @Named("instantToTimestamp")
    default Timestamp instantToTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
}