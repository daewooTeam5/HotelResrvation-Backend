package daewoo.team5.hotelreservation.domain.payment.projection;

import java.time.LocalDateTime;

public interface AdminPaymentProjection {
    Long getId();
    String getOrderId();
    String getPaymentKey();
    Long getAmount();
    LocalDateTime getTransactionDate();
    String getMethod();
    String getStatus();
    String getUserName(); // 회원이면 Users.name, 비회원이면 GuestEntity.firstName + lastName
}