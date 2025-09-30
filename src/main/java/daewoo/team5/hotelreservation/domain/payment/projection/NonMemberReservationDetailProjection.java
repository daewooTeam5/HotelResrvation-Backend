package daewoo.team5.hotelreservation.domain.payment.projection;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface NonMemberReservationDetailProjection {
    // Payment
    Long getPaymentId();
    String getPaymentKey();
    String getOrderId();
    Payment.PaymentStatus getStatus();
    Payment.PaymentMethod getMethod();
    Long getAmount();
    LocalDateTime getTransactionDate();

    // Reservation
    Long getReservationId();
    LocalDate getResevStart();
    LocalDate getResevEnd();
    String getRequest();
    BigDecimal getBaseAmount();
    BigDecimal getFinalAmount();
    Integer getFixedDiscountAmount();
    Integer getCouponDiscountAmount();
    Integer getPointDiscountAmount();

    // Place (Hotel)
    Long getPlaceId();
    String getPlaceName();
    LocalTime getCheckIn();

    // Room
    Long getRoomId();
    String getRoomType();
    BigDecimal getRoomPrice();

    // First room image
    String getFirstImageUrl();
    String getFirstName(); // 추가
    String getLastName();  // 추가
}