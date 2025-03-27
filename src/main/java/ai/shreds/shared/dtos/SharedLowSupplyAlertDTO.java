package ai.shreds.shared.dtos;

import ai.shreds.domain.entities.DomainEntityLowSupplyAlert;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedLowSupplyAlertDTO {

    @NotNull(message = "Alert ID cannot be null")
    private UUID alertId;

    @NotNull(message = "Item ID cannot be null")
    private UUID itemId;

    @NotNull(message = "Alert date cannot be null")
    private Timestamp alertDate;

    @NotBlank(message = "Alert message cannot be empty")
    private String alertMessage;

    public static SharedLowSupplyAlertDTO fromDomain(DomainEntityLowSupplyAlert entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Domain entity cannot be null");
        }
        return SharedLowSupplyAlertDTO.builder()
                .alertId(entity.getAlertId())
                .itemId(entity.getItemId())
                .alertDate(Timestamp.from(entity.getAlertDate()))
                .alertMessage(entity.getAlertMessage())
                .build();
    }
}
