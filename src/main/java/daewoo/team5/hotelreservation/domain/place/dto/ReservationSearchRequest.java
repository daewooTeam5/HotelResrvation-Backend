package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationSearchRequest {
    private String userName;      // 예약자 이름
    private String roomNo;        // 객실 번호
    private String hotelName;     // 호텔 이름
    private Reservation.ReservationStatus status;
    private Reservation.ReservationPaymentStatus paymentStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}