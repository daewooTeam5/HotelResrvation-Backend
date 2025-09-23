package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDTO {
    private long currentRevenue;  // 이번 달 매출액
    private long lastRevenue;     // 지난 달 매출액
    private double growthRate;    // 증감률 (%)
}
