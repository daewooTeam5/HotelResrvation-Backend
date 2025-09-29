package daewoo.team5.hotelreservation.domain.payment.projection;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;

import java.time.LocalDateTime;

public interface PaymentProjection {
    Long getId();
    String getOrderId();
    String getPaymentKey();
    Long getAmount();
    Payment.PaymentMethod getMethod();
    Payment.PaymentStatus getStatus();
    LocalDateTime getTransactionDate();
    String getMethodType();

    // 연관 예약 정보 일부 가져오기
    Long getReservation_reservationId();
}
