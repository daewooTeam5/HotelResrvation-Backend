package daewoo.team5.hotelreservation.domain.payment.projection;

import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PointHistorySummaryProjection {
    Long getId();
    PointHistoryEntity.PointType getType();
    Long getAmount();
    Long getBalanceAfter();
    LocalDate getExpireAt();
    LocalDateTime getCreatedAt();

    Long getReservationId();
    String getOrderId();
    String getHotelName();
    String getRoomType();
    String getBedType();
    String getDescription();
}

