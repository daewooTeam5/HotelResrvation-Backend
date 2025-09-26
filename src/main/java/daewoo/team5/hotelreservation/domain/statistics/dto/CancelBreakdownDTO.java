package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelBreakdownDTO {
    private long normalCount;    // 정상 예약 수
    private long cancelledCount; // 취소 예약 수
    private long refundedCount;  // 환불 예약 수
}
