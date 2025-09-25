package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardMetric {
    private long value;   // 실제 값 (예: 총 매출액, 총 유저 수 등)
    private long diff;    // 증가 수 (어제 대비, 지난달 대비 등)
}