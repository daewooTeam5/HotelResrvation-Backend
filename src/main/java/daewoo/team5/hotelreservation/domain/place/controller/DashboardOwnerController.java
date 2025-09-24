package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.service.DashboardOwnerService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 대시보드 API 컨트롤러 (숙소 주인용)
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardOwnerController {

    private final DashboardOwnerService dashboardOwnerService;

    /**
     * 오늘 예약 현황
     */
    @GetMapping("/stats/today")
    @AuthUser
    public ResponseEntity<ReservationStatsDTO> getTodayStats(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getTodayStats(projection.getId()));
    }

    /**
     * 최근 6개월 예약 현황
     */
    @GetMapping("/stats/monthly")
    @AuthUser
    public ResponseEntity<List<ReservationStatsDTO>> getMonthlyStats(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getMonthlyStats(projection.getId()));
    }

    @GetMapping("/stats/occupancy")
    @AuthUser
    public ResponseEntity<OccupancyRateDTO> getOccupancyRate(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getOccupancyRate(projection.getId()));
    }

    @GetMapping("/stats/rating")
    @AuthUser
    public ResponseEntity<RatingStatsDTO> getRatingStats(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getRatingStats(projection.getId()));
    }

    @GetMapping("/stats/revenue")
    @AuthUser
    public ResponseEntity<MonthlyRevenueDTO> getMonthlyRevenue(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getMonthlyRevenue(projection.getId()));
    }

    @GetMapping("/stats/revenue/monthly")
    @AuthUser
    public ResponseEntity<List<MonthlyRevenueChartDTO>> getMonthlyRevenueChart(
            UserProjection projection,
            @RequestParam(defaultValue = "6") int months
    ) {
        return ResponseEntity.ok(
                dashboardOwnerService.getMonthlyRevenueChart(projection.getId(), months)
        );
    }

    @GetMapping("/stats/reviews")
    @AuthUser
    public ResponseEntity<List<RecentReviewDTO>> getRecentReviews(UserProjection projection) {
        return ResponseEntity.ok(dashboardOwnerService.getRecentReviews(projection.getId()));
    }

}
