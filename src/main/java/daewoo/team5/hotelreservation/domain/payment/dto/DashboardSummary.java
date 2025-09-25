package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSummary {
    private DashboardMetric totalRevenue;       // 전체 매출
    private DashboardMetric todayReservations;  // 오늘 예약
    private DashboardMetric monthlyRevenue;     // 이번달 매출
    private DashboardMetric totalReservations;  // 총 예약 수
    private DashboardMetric totalUsers;         // 총 유저 수
}