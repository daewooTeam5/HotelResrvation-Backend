package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse;
import daewoo.team5.hotelreservation.domain.place.dto.SummaryResponse;
import daewoo.team5.hotelreservation.domain.place.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService statisticsService;

    /** 상단 요약 */
    @GetMapping("/summary")
    public SummaryResponse getMonthlySummary() {
        return statisticsService.getMonthlySummary();
    }

    /** 예약 추이 */
    @GetMapping("/reservations/daily")
    public List<ChartDataResponse> getDailyReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return statisticsService.getDailyReservations(start, end);
    }

    @GetMapping("/reservations/monthly")
    public List<ChartDataResponse> getMonthlyReservations() {
        return statisticsService.getMonthlyReservations();
    }

    @GetMapping("/reservations/yearly")
    public List<ChartDataResponse> getYearlyReservations() {
        return statisticsService.getYearlyReservations();
    }

    /** 매출 추이 */
    @GetMapping("/revenue/daily")
    public List<ChartDataResponse> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return statisticsService.getDailyRevenue(start, end);
    }

    @GetMapping("/revenue/monthly")
    public List<ChartDataResponse> getMonthlyRevenue() {
        return statisticsService.getMonthlyRevenue();
    }

    @GetMapping("/revenue/yearly")
    public List<ChartDataResponse> getYearlyRevenue() {
        return statisticsService.getYearlyRevenue();
    }

    /** 취소율 추이 */
    @GetMapping("/cancel-rate/daily")
    public List<ChartDataResponse> getDailyCancelRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return statisticsService.getDailyCancelRate(start, end);
    }

    @GetMapping("/cancel-rate/monthly")
    public List<ChartDataResponse> getMonthlyCancelRate() {
        return statisticsService.getMonthlyCancelRate();
    }

    @GetMapping("/cancel-rate/yearly")
    public List<ChartDataResponse> getYearlyCancelRate() {
        return statisticsService.getYearlyCancelRate();
    }

    /** 카테고리별 */
    @GetMapping("/revenue-by-category")
    public List<ChartDataResponse> getRevenueByCategory() {
        return statisticsService.getRevenueByCategory();
    }

    @GetMapping("/reservations-by-category")
    public List<ChartDataResponse> getReservationsByCategory() {
        return statisticsService.getReservationsByCategory();
    }
}