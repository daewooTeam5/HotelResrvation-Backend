package daewoo.team5.hotelreservation.domain.payment.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReservationInfoProjection {
    Long getReservationId();
    String getOrderId();
    String getStatus();          // ReservationStatus
    String getPaymentStatus();   // ReservationPaymentStatus
    BigDecimal getBaseAmount();
    BigDecimal getFinalAmount();
    LocalDate getResevStart();
    LocalDate getResevEnd();
    String getRequest();
}