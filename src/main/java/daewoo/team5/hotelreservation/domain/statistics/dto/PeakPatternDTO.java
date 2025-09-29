package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeakPatternDTO {
    private String label;     // 요일/월/연도 라벨
    private long count;       // 예약 건수
    private long revenue;     // 매출 (payments.amount 합계)
    private double occupancy; // 객실 점유율 (%)
}