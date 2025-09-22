package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationListDTO {
    private Long reservationId;
    private String guestName;       // 예약자 이름 (회원 or 비회원)
    private String roomType;        // 객실 유형
    private String status;          // 예약 상태
    private String paymentStatus;   // 결제 상태
    private LocalDate resevStart;
    private LocalDate resevEnd;
    private LocalDateTime createdAt; // ✅ 예약일
    private BigDecimal finalAmount;  // 최종 금액
    private boolean member;         // 회원 여부
}
