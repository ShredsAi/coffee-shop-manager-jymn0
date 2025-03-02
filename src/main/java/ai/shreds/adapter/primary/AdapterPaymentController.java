package ai.shreds.adapter.primary;

import ai.shreds.shared.dtos.SharedPaymentRequestDTO;
import ai.shreds.shared.dtos.SharedPaymentResponseDTO;
import ai.shreds.application.ports.ApplicationPaymentInputPort;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/payments")
public class AdapterPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AdapterPaymentController.class);
    private final ApplicationPaymentInputPort applicationPaymentInputPort;

    public AdapterPaymentController(ApplicationPaymentInputPort applicationPaymentInputPort) {
        this.applicationPaymentInputPort = applicationPaymentInputPort;
    }

    @PostMapping
    public ResponseEntity<SharedPaymentResponseDTO> createPayment(@Valid @RequestBody SharedPaymentRequestDTO request) {
        logger.info("Received payment request for order: {}", request.getOrderId());
        request.validate(); // Additional business rule validation
        SharedPaymentResponseDTO response = applicationPaymentInputPort.createPayment(request);
        logger.info("Payment processed for order: {}, status: {}", request.getOrderId(), response.getStatus());
        return response.toResponse();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<SharedPaymentResponseDTO> getPaymentStatus(@PathVariable UUID paymentId) {
        logger.info("Retrieving payment status for payment: {}", paymentId);
        SharedPaymentResponseDTO response = applicationPaymentInputPort.getPaymentStatus(paymentId);
        logger.info("Retrieved status for payment: {}, status: {}", paymentId, response.getStatus());
        return response.toResponse();
    }
}
