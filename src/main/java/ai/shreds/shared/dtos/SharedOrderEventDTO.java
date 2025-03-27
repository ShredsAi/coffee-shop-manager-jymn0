package ai.shreds.shared.dtos;

import ai.shreds.domain.entities.DomainEntityOrderEventCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedOrderEventDTO {

    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;

    @NotNull(message = "Item ID cannot be null")
    private UUID itemId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    public static Builder builder() {
        return new Builder();
    }

    public static SharedOrderEventDTO fromDomainCommand(DomainEntityOrderEventCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Domain command cannot be null");
        }
        return builder()
                .orderId(command.getOrderId())
                .itemId(command.getItemId())
                .quantity(command.getQuantity())
                .build();
    }

    public DomainEntityOrderEventCommand toDomainCommand() {
        return new DomainEntityOrderEventCommand.Builder()
                .orderId(this.orderId)
                .itemId(this.itemId)
                .quantity(this.quantity)
                .build();
    }

    public static class Builder {
        private final SharedOrderEventDTO instance = new SharedOrderEventDTO();

        public Builder orderId(UUID orderId) {
            instance.setOrderId(orderId);
            return this;
        }

        public Builder itemId(UUID itemId) {
            instance.setItemId(itemId);
            return this;
        }

        public Builder quantity(Integer quantity) {
            instance.setQuantity(quantity);
            return this;
        }

        public SharedOrderEventDTO build() {
            return instance;
        }
    }
}
