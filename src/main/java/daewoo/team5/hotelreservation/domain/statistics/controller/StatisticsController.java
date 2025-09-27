package daewoo.team5.hotelreservation.domain.statistics.controller;

import daewoo.team5.hotelreservation.domain.statistics.dto.*;
import daewoo.team5.hotelreservation.domain.statistics.service.StatisticsService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 오늘 예약 현황 (예약 수 + 전일 대비 증감률)
     */
    @GetMapping("/reservation/today")
    @AuthUser
    public ResponseEntity<TodayReservationDTO> getTodayReservationSummary(UserProjection projection) {
        Long ownerId = projection.getId();
        TodayReservationDTO dto = statisticsService.getTodayReservationSummary(ownerId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reservation/monthly")
    @AuthUser
    public ResponseEntity<MonthlyReservationDTO> getMonthlyReservationSummary(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getMonthlyReservationSummary(ownerId));
    }

    @GetMapping("/reservation/cancel-rate")
    @AuthUser
    public ResponseEntity<CancelRateDTO> getCancelRate(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getCancelRate(ownerId));
    }

    @GetMapping("/reservation/room-revenue")
    @AuthUser
    public ResponseEntity<List<RoomRevenueDTO>> getRoomRevenue(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getRoomRevenue(ownerId, startDate, endDate));
    }

    @GetMapping("/reservation/cancel-rate/breakdown")
    @AuthUser
    public ResponseEntity<CancelBreakdownDTO> getCancelBreakdown(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getCancelBreakdown(ownerId, startDate, endDate));
    }

    @GetMapping("/reservation/trend")
    @AuthUser
    public ResponseEntity<List<ReservationTrendDTO>> getReservationTrend(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String period // daily, weekly, monthly, yearly
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getReservationTrend(ownerId, startDate, endDate, period));
    }

    @GetMapping("/revenue/trend")
    @AuthUser
    public ResponseEntity<List<RevenueTrendDTO>> getRevenueTrend(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "daily") String period
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getRevenueTrend(ownerId, startDate, endDate, period));
    }

    @GetMapping("/payment/methods")
    @AuthUser
    public ResponseEntity<List<PaymentMethodStatsDTO>> getPaymentMethodStats(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getPaymentMethodStats(ownerId, startDate, endDate));
    }

    @GetMapping("/customers/new/today")
    @AuthUser
    public ResponseEntity<TodayNewGuestDTO> getTodayNewGuests(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getTodayNewGuests(ownerId));
    }

    @GetMapping("/customers/return/today")
    @AuthUser
    public ResponseEntity<TodayReturnGuestDTO> getTodayReturnGuests(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getTodayReturnGuests(ownerId));
    }

    @GetMapping("/customers/stay-duration/monthly")
    @AuthUser
    public ResponseEntity<StayDurationDTO> getAvgStayDuration(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getAvgStayDuration(ownerId));
    }

    @GetMapping("/customers/ratio")
    @AuthUser
    public ResponseEntity<GuestRatioDTO> getGuestRatio(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getGuestRatio(ownerId, startDate, endDate));
    }

    @GetMapping("/customers/stay-duration/distribution")
    @AuthUser
    public ResponseEntity<List<StayDurationDistributionDTO>> getStayDurationDistribution(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getStayDurationDistribution(ownerId, startDate, endDate));
    }

    @GetMapping("/customers/member-ratio")
    @AuthUser
    public ResponseEntity<MemberRatioDTO> getMemberRatio(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        MemberRatioDTO dto = statisticsService.getMemberRatio(ownerId, startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reviews/summary")
    @AuthUser
    public ResponseEntity<ReviewSummaryDTO> getReviewSummary(UserProjection projection) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getReviewSummary(ownerId));
    }

    @GetMapping("/reviews/distribution")
    @AuthUser
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getRatingDistribution(ownerId, startDate, endDate));
    }

    @GetMapping("/reviews/trend")
    @AuthUser
    public ResponseEntity<List<ReviewTrendDTO>> getReviewTrend(
            UserProjection projection,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "daily") String period // daily, weekly, monthly, yearly
    ) {
        Long ownerId = projection.getId();
        return ResponseEntity.ok(statisticsService.getReviewTrend(ownerId, startDate, endDate, period));
    }

}
