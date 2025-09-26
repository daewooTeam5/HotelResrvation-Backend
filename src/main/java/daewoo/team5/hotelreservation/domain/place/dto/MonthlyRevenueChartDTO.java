package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueChartDTO {
    private String month;   // "2025-09" 같은 문자열
    private long revenue;   // 매출 합계
}
