package daewoo.team5.hotelreservation.domain.statistics.controller;

import daewoo.team5.hotelreservation.domain.statistics.dto.StatisticsResponse;
import daewoo.team5.hotelreservation.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 예약/매출 통계
    @GetMapping("/reservations")
    public StatisticsResponse getReservationStats(
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "all") String type
    ) {
        return statisticsService.getReservationStats(period, start, end, type);
    }

    // 고객 통계
    @GetMapping("/customers")
    public StatisticsResponse getCustomerStats(
            @RequestParam(defaultValue = "monthly") String period
    ) {
        return statisticsService.getCustomerStats(period);
    }

    // 리뷰 통계
    @GetMapping("/reviews")
    public StatisticsResponse getReviewStats(
            @RequestParam(defaultValue = "monthly") String period
    ) {
        return statisticsService.getReviewStats(period);
    }

    // 운영/객실 관리 통계
    @GetMapping("/rooms")
    public StatisticsResponse getRoomStats(
            @RequestParam(defaultValue = "monthly") String period
    ) {
        return statisticsService.getRoomStats(period);
    }
}