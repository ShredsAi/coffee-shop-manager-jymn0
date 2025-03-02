package ai.shreds.application.ports;

import java.math.BigDecimal;
import java.util.UUID;

public interface ApplicationPaymentOutputPort {

    void notifyOrderService(UUID paymentId, String status);

    void notifyFinancialService(UUID paymentId, BigDecimal amount);
}
