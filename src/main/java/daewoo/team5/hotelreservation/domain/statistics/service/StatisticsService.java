package daewoo.team5.hotelreservation.domain.statistics.service;

import daewoo.team5.hotelreservation.domain.place.dto.ReservationStatsDTO;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.service.DashboardOwnerService;
import daewoo.team5.hotelreservation.domain.statistics.dto.CancelRateDTO;
import daewoo.team5.hotelreservation.domain.statistics.dto.MonthlyReservationDTO;
import daewoo.team5.hotelreservation.domain.statistics.dto.RoomRevenueDTO;
import daewoo.team5.hotelreservation.domain.statistics.dto.TodayReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DashboardOwnerService dashboardOwnerService;
    private final ReservationRepository reservationRepository;

    public TodayReservationDTO getTodayReservationSummary(Long ownerId) {
        ReservationStatsDTO stats = dashboardOwnerService.getTodayStats(ownerId);
        return new TodayReservationDTO(
                stats.getTodayReservations(),
                stats.getGrowthRate()
        );
    }

    public MonthlyReservationDTO getMonthlyReservationSummary(Long ownerId) {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        long thisMonthReservations = reservationRepository.countByOwnerIdAndMonth(
                ownerId, thisMonth.getYear(), thisMonth.getMonthValue()
        );

        long lastMonthReservations = reservationRepository.countByOwnerIdAndMonth(
                ownerId, lastMonth.getYear(), lastMonth.getMonthValue()
        );

        double growthRate = lastMonthReservations > 0
                ? ((double)(thisMonthReservations - lastMonthReservations) / lastMonthReservations) * 100
                : (thisMonthReservations > 0 ? 100.0 : 0.0);

        return new MonthlyReservationDTO(thisMonthReservations, growthRate);
    }

    public CancelRateDTO getCancelRate(Long ownerId) {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        // 이번 달
        long thisTotal = reservationRepository.countTotalReservationsByOwnerAndMonth(
                ownerId, thisMonth.getYear(), thisMonth.getMonthValue());
        long thisCancelled = reservationRepository.countCancelledOrRefundedReservationsByOwnerAndMonth(
                ownerId, thisMonth.getYear(), thisMonth.getMonthValue());

        // 지난 달
        long lastTotal = reservationRepository.countTotalReservationsByOwnerAndMonth(
                ownerId, lastMonth.getYear(), lastMonth.getMonthValue());
        long lastCancelled = reservationRepository.countCancelledOrRefundedReservationsByOwnerAndMonth(
                ownerId, lastMonth.getYear(), lastMonth.getMonthValue());

        double thisCancelRate = thisTotal > 0 ? ((double) thisCancelled / thisTotal) * 100 : 0.0;
        double lastCancelRate = lastTotal > 0 ? ((double) lastCancelled / lastTotal) * 100 : 0.0;

        double growthRate = lastCancelRate > 0
                ? ((thisCancelRate - lastCancelRate) / lastCancelRate) * 100
                : (thisCancelRate > 0 ? 100.0 : 0.0);

        return new CancelRateDTO(thisCancelRate, growthRate);
    }

    public List<RoomRevenueDTO> getRoomRevenue(Long ownerId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = reservationRepository.findRoomRevenueByOwnerAndPeriod(ownerId, startDate, endDate);

        return results.stream()
                .map(row -> new RoomRevenueDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

}