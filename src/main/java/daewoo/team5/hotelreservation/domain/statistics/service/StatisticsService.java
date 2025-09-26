package daewoo.team5.hotelreservation.domain.statistics.service;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationStatsDTO;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.service.DashboardOwnerService;
import daewoo.team5.hotelreservation.domain.statistics.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DashboardOwnerService dashboardOwnerService;
    private final ReservationRepository reservationRepository;

    private static final String[] WEEK_DAYS = {"일", "월", "화", "수", "목", "금", "토"};
    private final PaymentRepository paymentRepository;

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

    public CancelBreakdownDTO getCancelBreakdown(Long ownerId, LocalDate startDate, LocalDate endDate) {
        long normal = reservationRepository.countNormalReservationsByOwnerAndPeriod(ownerId, startDate, endDate);
        long cancelled = reservationRepository.countCancelledReservationsByOwnerAndPeriod(ownerId, startDate, endDate);
        long refunded = reservationRepository.countRefundedReservationsByOwnerAndPeriod(ownerId, startDate, endDate);

        return new CancelBreakdownDTO(normal, cancelled, refunded);
    }

    public List<ReservationTrendDTO> getReservationTrend(Long ownerId,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         String period) {
        List<Object[]> results;

        switch (period.toLowerCase()) {
            case "daily":
                results = reservationRepository.countDailyReservations(ownerId, startDate, endDate);
                break;
            case "weekly":
                results = reservationRepository.countWeeklyReservations(ownerId, startDate, endDate);
                break;
            case "monthly":
                results = reservationRepository.countMonthlyReservations(ownerId, startDate, endDate);
                break;
            case "yearly":
                results = reservationRepository.countYearlyReservations(ownerId, startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        return results.stream()
                .map(row -> new ReservationTrendDTO(row[0].toString(), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<RevenueTrendDTO> getRevenueTrend(Long ownerId, LocalDate startDate, LocalDate endDate, String period) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results = switch (period) {
            case "daily" -> paymentRepository.findDailyRevenue(ownerId, startDateTime, endDateTime);
            case "weekly" -> paymentRepository.findWeeklyRevenue(ownerId, startDateTime, endDateTime);
            case "monthly" -> paymentRepository.findMonthlyRevenue(ownerId, startDateTime, endDateTime);
            case "yearly" -> paymentRepository.findYearlyRevenue(ownerId, startDateTime, endDateTime);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };

        return results.stream()
                .map(row -> new RevenueTrendDTO(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public List<PaymentMethodStatsDTO> getPaymentMethodStats(Long ownerId,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results = paymentRepository.findPaymentMethodStats(ownerId, startDateTime, endDateTime);

        return results.stream()
                .map(row -> new PaymentMethodStatsDTO(
                        Enum.valueOf(Payment.PaymentMethod.class, row[0].toString()),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    public TodayNewGuestDTO getTodayNewGuests(Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long todayNewGuests = reservationRepository.countNewGuestsByOwnerAndCreatedAtBetween(
                ownerId, startOfDay, endOfDay
        );

        LocalDate yesterday = today.minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.atTime(23, 59, 59);

        long yesterdayNewGuests = reservationRepository.countNewGuestsByOwnerAndCreatedAtBetween(
                ownerId, yesterdayStart, yesterdayEnd
        );

        double growthRate = yesterdayNewGuests > 0
                ? ((double)(todayNewGuests - yesterdayNewGuests) / yesterdayNewGuests) * 100
                : (todayNewGuests > 0 ? 100.0 : 0.0);

        return new TodayNewGuestDTO(todayNewGuests, growthRate);
    }

    public TodayReturnGuestDTO getTodayReturnGuests(Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        long todayReturn = reservationRepository.countTodayReturnGuests(ownerId, today);
        long yesterdayReturn = reservationRepository.countTodayReturnGuests(ownerId, yesterday);

        double growthRate = yesterdayReturn > 0
                ? ((double)(todayReturn - yesterdayReturn) / yesterdayReturn) * 100
                : (todayReturn > 0 ? 100.0 : 0.0);

        return new TodayReturnGuestDTO(todayReturn, growthRate);
    }

    public StayDurationDTO getAvgStayDuration(Long ownerId) {
        LocalDate now = LocalDate.now();
        int thisYear = now.getYear();
        int thisMonth = now.getMonthValue();

        // 이번 달 평균
        Double thisMonthAvg = reservationRepository.findAvgStayDurationByOwnerAndMonth(ownerId, thisYear, thisMonth);
        if (thisMonthAvg == null) thisMonthAvg = 0.0;

        // 전월 평균
        LocalDate lastMonth = now.minusMonths(1);
        Double lastMonthAvg = reservationRepository.findAvgStayDurationByOwnerAndMonth(ownerId, lastMonth.getYear(), lastMonth.getMonthValue());
        if (lastMonthAvg == null) lastMonthAvg = 0.0;

        // 증감률 계산
        double growthRate = (lastMonthAvg == 0) ? 0 : ((thisMonthAvg - lastMonthAvg) / lastMonthAvg) * 100;

        return new StayDurationDTO(thisMonthAvg, growthRate);
    }

    public GuestRatioDTO getGuestRatio(Long ownerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        long newGuests = reservationRepository.countNewGuestsByPeriod(ownerId, startDateTime, endDateTime);
        long returnGuests = reservationRepository.countReturnGuestsByPeriod(ownerId, startDateTime, endDateTime);

        return new GuestRatioDTO(newGuests, returnGuests);
    }

    public List<StayDurationDistributionDTO> getStayDurationDistribution(Long ownerId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = reservationRepository.findStayDurationDistribution(ownerId, startDate, endDate);

        return results.stream()
                .map(row -> new StayDurationDistributionDTO(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public MemberRatioDTO getMemberRatio(Long ownerId, LocalDate startDate, LocalDate endDate) {
        long members = reservationRepository.countDistinctMembers(ownerId, startDate, endDate);
        long nonMembers = reservationRepository.countDistinctNonMembers(ownerId, startDate, endDate);

        return new MemberRatioDTO(members, nonMembers);
    }

}