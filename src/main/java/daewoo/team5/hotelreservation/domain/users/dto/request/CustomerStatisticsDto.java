package daewoo.team5.hotelreservation.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomerStatisticsDto {
    private long totalUsers;             // 총 고객 수
    private long newUsersThisMonth;      // 신규 고객
    private long activeUsers;            // 예약/결제 고객 수
    private double repeatRate;           // 재구매율 (%)

    private long withdrawnUsers;
    private double withdrawalRate;
    // 고객 활동 통계
    private List<TopCustomerDto> topReservationCustomers;
    private List<TopCustomerDto> topPaymentCustomers;
    private double avgReservations;
    private double avgPayments;

    // 고객 세분화
    private List<Long> vipCustomers;     // 상위 10%
    private List<Long> newCustomers;     // 최근 30일 이내
    private List<Long> dormantCustomers; // 최근 6개월간 활동 없음

    // 고객 행동 트렌드
    private List<MonthlyCountDto> monthlyNewUsers;
    private List<RetentionDto> monthlyRetention;
}
