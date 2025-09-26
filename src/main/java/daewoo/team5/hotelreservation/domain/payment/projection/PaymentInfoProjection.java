package daewoo.team5.hotelreservation.domain.payment.projection;

import java.time.LocalDateTime;

public interface PaymentInfoProjection {
    Long getId();
    String getOrderId();
    String getPaymentKey();
    Long getAmount();
    LocalDateTime getTransactionDate();
    String getMethod();    // PaymentMethod
    String getStatus();    // PaymentStatus
    String getMethodType();
}