package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse;

import daewoo.team5.hotelreservation.domain.place.dto.SummaryResponse;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public SummaryResponse getMonthlySummary() {
        YearMonth now = YearMonth.now();

        // Reservation은 LocalDate
        LocalDate startDate = now.atDay(1);
        LocalDate endDate = now.atEndOfMonth();

        long reservationCount = reservationRepository.countReservationsBetween(startDate, endDate);
        long cancelledCount = reservationRepository.countCancelledReservationsBetween(startDate, endDate);

        // Payment는 LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Long revenue = paymentRepository.getRevenueBetween(startDateTime, endDateTime);
        if (revenue == null) revenue = 0L;

        double cancelRate = reservationCount > 0
                ? (cancelledCount * 100.0 / reservationCount)
                : 0.0;

        return new SummaryResponse(reservationCount, revenue, cancelRate);
    }

    /** 예약 */
    public List<ChartDataResponse> getDailyReservations(LocalDate start, LocalDate end) {
        return reservationRepository.getDailyReservations(start, end);
    }
    public List<ChartDataResponse> getMonthlyReservations() {
        return reservationRepository.getMonthlyReservations();
    }
    public List<ChartDataResponse> getYearlyReservations() {
        return reservationRepository.getYearlyReservations();
    }

    /** 매출 */
    public List<ChartDataResponse> getDailyRevenue(LocalDate start, LocalDate end) {
        // LocalDate → LocalDateTime 변환해서 Repository 호출
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        return paymentRepository.getDailyRevenue(startDateTime, endDateTime);
    }

    public List<ChartDataResponse> getMonthlyRevenue() {
        return paymentRepository.getMonthRevenue();
    }
    public List<ChartDataResponse> getYearlyRevenue() {
        return paymentRepository.getYearlyRevenue();
    }

    /** 취소율 */
    public List<ChartDataResponse> getDailyCancelRate(LocalDate start, LocalDate end) {
        return reservationRepository.getDailyCancelRate(start, end);
    }
    public List<ChartDataResponse> getMonthlyCancelRate() {
        return reservationRepository.getMonthlyCancelRate();
    }
    public List<ChartDataResponse> getYearlyCancelRate() {
        return reservationRepository.getYearlyCancelRate();
    }

    /** 카테고리별 */
    public List<ChartDataResponse> getRevenueByCategory() {
        return paymentRepository.getRevenueByCategory();
    }
    public List<ChartDataResponse> getReservationsByCategory() {
        return reservationRepository.getReservationsByCategory();
    }
}
