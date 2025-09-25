package daewoo.team5.hotelreservation.domain.payment.service;

import daewoo.team5.hotelreservation.domain.payment.dto.*;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;

    public DashboardSummary getDashboardSummary() {
        // ===== 날짜 구간 세팅 =====
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);

        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.plusDays(1).atStartOfDay().minusNanos(1);

        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastMonth = today.minusMonths(1);
        LocalDate firstDayOfLastMonth = lastMonth.withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

        // ===== 매출 =====
        long totalPayment = paymentRepository.getTotalPayments();
        long todayPayment = paymentRepository.getPaymentsBetween(todayStart, todayEnd);
        long yesterdayPayment = paymentRepository.getPaymentsBetween(yesterdayStart, yesterdayEnd);
        long thisMonthPayment = paymentRepository.getPaymentsBetween(
                firstDayOfMonth.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1));
        long lastMonthPayment = paymentRepository.getPaymentsBetween(
                firstDayOfLastMonth.atStartOfDay(),
                lastDayOfLastMonth.atTime(23, 59, 59));

        // ===== 예약 =====
        long todayReservations = reservationRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long yesterdayReservations = reservationRepository.countByCreatedAtBetween(yesterdayStart, yesterdayEnd);
        long totalReservations = reservationRepository.count();

        // ===== 유저 =====
        long todayUsers = usersRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long yesterdayUsers = usersRepository.countByCreatedAtBetween(yesterdayStart, yesterdayEnd);
        long totalUsers = usersRepository.count();

        // 증가 수 계산
        long totalRevenueDiff = totalPayment - yesterdayPayment;          // 전체 매출: 어제 대비
        long todayReservationDiff = todayReservations - yesterdayReservations; // 오늘 예약: 어제 대비
        long monthlyRevenueDiff = thisMonthPayment - lastMonthPayment;    // 이번달 매출: 지난달 대비
        long totalUserDiff = todayUsers - yesterdayUsers;                 // 총 유저: 어제 대비

        // ===== DashboardSummary 반환 =====
        return new DashboardSummary(
                new DashboardMetric(totalPayment, totalRevenueDiff),
                new DashboardMetric(todayReservations, todayReservationDiff),
                new DashboardMetric(thisMonthPayment, monthlyRevenueDiff),
                new DashboardMetric(totalReservations, 0), // 총 예약 수는 diff 없음
                new DashboardMetric(totalUsers, totalUserDiff)
        );
    }

    public List<MonthlyRevenueDto> getMonthlyRevenueTrend() {
        return paymentRepository.getMonthlyRevenue().stream()
                .map(r -> new MonthlyRevenueDto((Integer) r[0], (Integer) r[1], ((Number) r[2]).longValue()))
                .toList();
    }

    public List<TopHotelDto> getTop5HotelsByRevenue() {
        return paymentRepository.getTop5HotelsByRevenue().stream()
                .map(r -> new TopHotelDto((String) r[0], ((Number) r[1]).longValue()))
                .limit(5) // 혹시 DB에서 LIMIT 안먹히는 JPA 구현체 대비
                .toList();
    }

    /**
     * 호텔 예약 TOP 5
     */
    public List<TopHotelDto> getTop5HotelsByReservations() {
        return paymentRepository.getTop5HotelsByReservations().stream()
                .map(r -> new TopHotelDto((String) r[0], ((Number) r[1]).longValue()))
                .limit(5)
                .toList();
    }


    public List<OccupancyRateDto> getRegionReservationDistribution() {
        LocalDate today = LocalDate.now();
        List<Object[]> raw = dailyPlaceReservationRepository.getRegionReservationDistribution(today);

        long total = raw.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();

        return raw.stream()
                .map(r -> {
                    String region = (String) r[0];
                    long count = ((Number) r[1]).longValue();
                    double percent = total > 0 ? (count * 100.0 / total) : 0.0;
                    return new OccupancyRateDto(region, percent);
                })
                .toList();
    }
}
