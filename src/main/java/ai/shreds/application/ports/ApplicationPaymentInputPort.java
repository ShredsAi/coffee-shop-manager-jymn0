package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedPaymentRequestDTO;
import ai.shreds.shared.dtos.SharedPaymentResponseDTO;
import java.util.UUID;

public interface ApplicationPaymentInputPort {

    SharedPaymentResponseDTO createPayment(SharedPaymentRequestDTO request);

    SharedPaymentResponseDTO getPaymentStatus(UUID paymentId);
}
