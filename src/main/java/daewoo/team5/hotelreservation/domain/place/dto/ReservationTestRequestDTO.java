package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 관리자 테스트용 예약 생성 DTO
 */
@Getter
@Setter
public class ReservationTestRequestDTO {
    private Long roomId;             // 객실 유형 ID
    private LocalDateTime resevStart;
    private LocalDateTime resevEnd;
    private BigDecimal baseAmount;
    private BigDecimal finalAmount;
    private String request;          // 요청사항 (optional)
}
