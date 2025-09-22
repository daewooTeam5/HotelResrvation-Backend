package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationSearchRequest {
    private String userName;       // 예약자 이름
    private String email;          // 이메일
    private String phone;          // 전화번호
    private Long reservationId;    // 예약 ID
    private String hotelName;      // 호텔 이름
    private String roomType;       // 객실 유형
    private String status;         // 예약 상태
    private String paymentStatus;  // 결제 상태
    private LocalDate startDate;   // 체크인 시작일
    private LocalDate endDate;     // 체크아웃 종료일
    private BigDecimal minAmount;  // 최소 금액
    private BigDecimal maxAmount;  // 최대 금액

    private LocalDate createdStartDate; // 예약 생성 시작일
    private LocalDate createdEndDate;   // 예약 생성 종료일
}