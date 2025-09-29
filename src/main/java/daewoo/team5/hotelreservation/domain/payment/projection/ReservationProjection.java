package daewoo.team5.hotelreservation.domain.payment.projection;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReservationProjection {
    Long getReservationId();
    String getOrderId();
    Reservation.ReservationStatus getStatus();
    Reservation.ReservationPaymentStatus getPaymentStatus();
    BigDecimal getBaseAmount();
    BigDecimal getFinalAmount();
    LocalDate getResevStart();
    LocalDate getResevEnd();
    String getRequest();

    // 연관 엔티티 정보
    String getRoom_roomType();   // Room.roomType
    String getRoom_bedType();    // Room.bedType
    Integer getRoom_capacityPeople();
    String getRoom_place_name(); // 숙소 이름
}