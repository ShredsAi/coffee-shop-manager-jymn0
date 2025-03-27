package ai.shreds.application.services;

import ai.shreds.shared.dtos.SharedInventoryItemDTO;
import ai.shreds.shared.dtos.SharedOrderEventDTO;
import ai.shreds.domain.entities.DomainEntityInventoryItem;
import ai.shreds.domain.entities.DomainEntityOrderEventCommand;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.domain.value_objects.DomainValueThreshold;
import ai.shreds.application.exceptions.ApplicationInventoryException;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class ApplicationInventoryMapper {

    public static final ApplicationInventoryMapper INSTANCE = Mappers.getMapper(ApplicationInventoryMapper.class);

    @Mapping(source = "quantity.value", target = "quantity")
    @Mapping(source = "threshold.value", target = "threshold")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToTimestamp")
    public abstract SharedInventoryItemDTO toDTO(DomainEntityInventoryItem domainEntity);

    @Named("instantToTimestamp")
    protected Timestamp instantToTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    public DomainEntityInventoryItem toDomain(SharedInventoryItemDTO dto) {
        validateDTO(dto);
        return new DomainEntityInventoryItem.Builder()
                .itemId(dto.getItemId())
                .itemName(dto.getItemName())
                .quantity(toQuantityValue(dto.getQuantity()))
                .threshold(toThresholdValue(dto.getThreshold()))
                .updatedAt(dto.getUpdatedAt().toInstant())
                .build();
    }

    public DomainEntityOrderEventCommand toDomainCommand(SharedOrderEventDTO dto) {
        if (dto == null) {
            throw ApplicationInventoryException.invalidInput("Order event DTO cannot be null");
        }
        return new DomainEntityOrderEventCommand.Builder()
                .orderId(dto.getOrderId())
                .itemId(dto.getItemId())
                .quantity(dto.getQuantity())
                .build();
    }

    @Named("toQuantityValue")
    public DomainValueQuantity toQuantityValue(Integer value) {
        try {
            if (value == null) {
                throw ApplicationInventoryException.invalidInput("Quantity value cannot be null");
            }
            return new DomainValueQuantity(value);
        } catch (ApplicationInventoryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error converting quantity value: {}", value, e);
            throw ApplicationInventoryException.mappingError("Invalid quantity value: " + value, e);
        }
    }

    @Named("toThresholdValue")
    public DomainValueThreshold toThresholdValue(Integer value) {
        try {
            if (value == null) {
                throw ApplicationInventoryException.invalidInput("Threshold value cannot be null");
            }
            return new DomainValueThreshold(value);
        } catch (ApplicationInventoryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error converting threshold value: {}", value, e);
            throw ApplicationInventoryException.mappingError("Invalid threshold value: " + value, e);
        }
    }

    public void validateEntity(DomainEntityInventoryItem entity) {
        if (entity == null) {
            throw ApplicationInventoryException.invalidInput("Domain entity cannot be null");
        }
        if (entity.getItemId() == null) {
            throw ApplicationInventoryException.invalidInput("Item ID cannot be null");
        }
        if (entity.getItemName() == null || entity.getItemName().trim().isEmpty()) {
            throw ApplicationInventoryException.invalidInput("Item name cannot be null or empty");
        }
        if (entity.getQuantity() == null) {
            throw ApplicationInventoryException.invalidInput("Quantity cannot be null");
        }
        if (entity.getThreshold() == null) {
            throw ApplicationInventoryException.invalidInput("Threshold cannot be null");
        }
        if (entity.getUpdatedAt() == null) {
            throw ApplicationInventoryException.invalidInput("Updated timestamp cannot be null");
        }
    }

    public void validateDTO(SharedInventoryItemDTO dto) {
        if (dto == null) {
            throw ApplicationInventoryException.invalidInput("DTO cannot be null");
        }
        if (dto.getItemId() == null) {
            throw ApplicationInventoryException.invalidInput("Item ID cannot be null in DTO");
        }
        if (dto.getItemName() == null || dto.getItemName().trim().isEmpty()) {
            throw ApplicationInventoryException.invalidInput("Item name cannot be null or empty in DTO");
        }
        if (dto.getQuantity() == null) {
            throw ApplicationInventoryException.invalidInput("Quantity cannot be null in DTO");
        }
        if (dto.getThreshold() == null) {
            throw ApplicationInventoryException.invalidInput("Threshold cannot be null in DTO");
        }
        if (dto.getUpdatedAt() == null) {
            throw ApplicationInventoryException.invalidInput("Updated timestamp cannot be null in DTO");
        }
    }
}