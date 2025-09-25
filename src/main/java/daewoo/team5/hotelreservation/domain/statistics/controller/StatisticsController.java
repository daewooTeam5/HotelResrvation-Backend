package daewoo.team5.hotelreservation.domain.statistics.controller;

import daewoo.team5.hotelreservation.domain.statistics.dto.CancelRateDTO;
import daewoo.team5.hotelreservation.domain.statistics.dto.MonthlyReservationDTO;
import daewoo.team5.hotelreservation.domain.statistics.dto.TodayReservationDTO;
import daewoo.team5.hotelreservation.domain.statistics.service.StatisticsService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
