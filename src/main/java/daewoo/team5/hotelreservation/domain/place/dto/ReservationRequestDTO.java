package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Builder
public class ReservationRequestDTO {
    private Long userId;            // 회원 예약일 경우
    private Long guestId;           // 비회원 예약일 경우
    private Long roomId;

    private LocalDate resevStart;
    private LocalDate resevEnd;
    private Long resevAmount;

    private BigDecimal baseAmount;
    private BigDecimal finalAmount;

    private String status;          // 예약 상태
    private String paymentStatus;   // 결제 상태
    private String request;         // 요청사항
}