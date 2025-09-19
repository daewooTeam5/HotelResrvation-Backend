package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDetailDTO {
    // 예약 기본 정보
    private Long reservationId;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;  // ✅ 예약일
    private String request;

    // 예약자 정보
    private Long userId;
    private boolean member;
    private Long guestId;
    private String guestName;
    private String email;   // 상세에서만 노출
    private String phone;   // 상세에서만 노출

    // 객실 정보
    private Long roomId;
    private String roomType;
    private Integer capacityPeople;
    private BigDecimal price;

    // 예약 기간 및 금액
    private LocalDateTime resevStart;
    private LocalDateTime resevEnd;
    private Long resevAmount;
    private BigDecimal baseAmount;
    private BigDecimal finalAmount;

    // 결제 정보
    private Long paymentId;
    private String method;
    private String paymentStatusDetail;
    private Long paymentAmount;
    private LocalDateTime transactionDate;
}
