package daewoo.team5.hotelreservation.domain.payment.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PointProjection {
    Long getId();
    Long getUserId();
    String getType();           // EARN / USE
    Integer getAmount();        // 변환된 포인트
    Integer getBalanceAfter();  // 거래 후 포인트
    LocalDate getExpireAt();    // 만료일
    LocalDateTime getCreatedAt();
    Long getReservationId();    // 예약 ID (nullable)
}
