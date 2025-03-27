package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationOrderEventInputPort;
import ai.shreds.shared.dtos.SharedOrderEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AdapterOrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AdapterOrderEventListener.class);
    private final ApplicationOrderEventInputPort orderEventService;

    public AdapterOrderEventListener(ApplicationOrderEventInputPort orderEventService) {
        this.orderEventService = orderEventService;
    }

    @RabbitListener(queues = "${inventory.queue.orders}")
    public void handleOrderEvent(SharedOrderEventDTO event) {
        try {
            logger.info("Received order event for item: {}", event.getItemId());
            orderEventService.handleOrderEvent(event);
            logger.info("Successfully processed order event for item: {}", event.getItemId());
        } catch (Exception e) {
            logger.error("Error processing order event for item: {}", event.getItemId(), e);
            throw new AdapterInventoryException("Failed to process order event: " + e.getMessage());
        }
    }
}
