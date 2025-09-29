package daewoo.team5.hotelreservation.domain.statistics.service;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationStatsDTO;
import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
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
    private final ReviewRepository reviewRepository;

    private final RoomRepository roomRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;

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

    public ReviewSummaryDTO getReviewSummary(Long ownerId) {
        // 전체 평균 평점
        Double avgRating = reviewRepository.findAvgRatingByOwner(ownerId);
        if (avgRating == null) avgRating = 0.0;

        // 전체 리뷰 수
        long totalReviews = reviewRepository.countByOwner(ownerId);

        // 전체 예약 수
        long totalReservations = reservationRepository.countByOwner(ownerId);

        // 리뷰 작성률
        double reviewRate = totalReservations > 0
                ? ((double) totalReviews / totalReservations) * 100
                : 0.0;

        return new ReviewSummaryDTO(avgRating, totalReviews, reviewRate);
    }

    public Map<Integer, Long> getRatingDistribution(Long ownerId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = reviewRepository.findRatingDistribution(ownerId, startDate.atStartOfDay(), endDate.atTime(23,59,59));

        Map<Integer, Long> distribution = new HashMap<>();
        for (Object[] row : results) {
            Integer rating = (Integer) row[0];
            Long count = ((Number) row[1]).longValue();
            distribution.put(rating, count);
        }

        // 1~5점까지 빠진 값은 0으로 채워줌
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return distribution;
    }

    public List<ReviewTrendDTO> getReviewTrend(Long ownerId, LocalDate startDate, LocalDate endDate, String period) {
        List<Object[]> results;

        switch (period.toLowerCase()) {
            case "daily" ->
                    results = reviewRepository.countDailyReviews(ownerId, startDate, endDate);
            case "weekly" ->
                    results = reviewRepository.countWeeklyReviews(ownerId, startDate, endDate);
            case "monthly" ->
                    results = reviewRepository.countMonthlyReviews(ownerId, startDate, endDate);
            case "yearly" ->
                    results = reviewRepository.countYearlyReviews(ownerId, startDate, endDate);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        }

        return results.stream()
                .map(r -> new ReviewTrendDTO(r[0].toString(), ((Number) r[1]).longValue()))
                .toList();
    }

    public List<DailyAvailabilityDTO> getAvailability(Long ownerId) {
        List<Room> rooms = roomRepository.findAllByOwnerId(ownerId);

        // DailyPlaceReservation 전체 조회
        List<DailyPlaceReservation> reservations = dailyPlaceReservationRepository.findAll();

        // roomId + date 기준으로 map 구성
        Map<String, DailyPlaceReservation> dprMap = reservations.stream()
                .collect(Collectors.toMap(
                        d -> d.getRoom().getId() + "_" + d.getDate(),
                        d -> d
                ));

        Map<LocalDate, List<RoomAvailabilityDTO>> availabilityMap = new HashMap<>();

        for (Room room : rooms) {
            // DailyPlaceReservation이 기록된 모든 날짜 가져오기
            Set<LocalDate> dates = reservations.stream()
                    .map(DailyPlaceReservation::getDate)
                    .collect(Collectors.toSet());

            for (LocalDate date : dates) {
                String key = room.getId() + "_" + date;
                DailyPlaceReservation dpr = dprMap.get(key);

                int total = room.getCapacityRoom();
                int available = (dpr != null) ? dpr.getAvailableRoom() : total;

                RoomAvailabilityDTO roomDTO = RoomAvailabilityDTO.builder()
                        .date(date)
                        .roomType(room.getRoomType())
                        .available(available)
                        .total(total)
                        .build();

                availabilityMap
                        .computeIfAbsent(date, k -> new ArrayList<>())
                        .add(roomDTO);
            }
        }

        return availabilityMap.entrySet().stream()
                .map(entry -> new DailyAvailabilityDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailyAvailabilityDTO::getDate))
                .toList();
    }

    /**
     * 전체 객실 수 + 점유율
     */
    public RoomSummaryDTO getRoomSummary(Long ownerId) {
        long totalRooms = roomRepository.countTotalRoomsByOwner(ownerId);

        // 현재 예약된 객실 수 (오늘 기준)
        long reservedRooms = reservationRepository.countNormalReservationsByOwnerAndPeriod(
                ownerId,
                java.time.LocalDate.now(),
                java.time.LocalDate.now()
        );

        double occupancyRate = (totalRooms > 0)
                ? ((double) reservedRooms / totalRooms) * 100
                : 0.0;

        return new RoomSummaryDTO(totalRooms, occupancyRate);
    }

    /**
     * 객실 상태 분포
     */
    public RoomStatusDTO getRoomStatus(Long ownerId) {
        List<Object[]> results = roomRepository.countRoomStatusWithTypesByOwner(ownerId);

        long available = 0, reserved = 0, cleaning = 0;
        List<String> availableTypes = new ArrayList<>();
        List<String> reservedTypes = new ArrayList<>();
        List<String> cleaningTypes = new ArrayList<>();

        for (Object[] row : results) {
            String status = row[0].toString();
            String roomType = row[1].toString();
            long count = ((Number) row[2]).longValue();

            switch (status) {
                case "AVAILABLE" -> {
                    available += count;
                    availableTypes.add(roomType);
                }
                case "RESERVED" -> {
                    reserved += count;
                    reservedTypes.add(roomType);
                }
                case "CLEANING" -> {
                    cleaning += count;
                    cleaningTypes.add(roomType);
                }
            }
        }

        return new RoomStatusDTO(
                available, reserved, cleaning,
                availableTypes, reservedTypes, cleaningTypes
        );
    }

    public List<PeakPatternDTO> getPeakPattern(Long ownerId, String type) {
        List<Object[]> results;

        switch (type) {
            case "weekday" -> results = reservationRepository.countReservationsByWeekday(ownerId);
            case "monthly_pattern" -> results = reservationRepository.countReservationsByMonth(ownerId);
            case "yearly_pattern" -> results = reservationRepository.countReservationsByYear(ownerId);
            default -> throw new IllegalArgumentException("Invalid peak pattern type: " + type);
        }

        return results.stream()
                .map(r -> {
                    String label;
                    if ("weekday".equals(type)) {
                        int dayNum = ((Number) r[0]).intValue(); // 1~7
                        label = WEEK_DAYS[dayNum - 1];
                    } else {
                        label = r[0].toString();
                    }
                    return new PeakPatternDTO(
                            label,
                            ((Number) r[1]).longValue(),   // count
                            ((Number) r[2]).longValue(),   // revenue
                            ((Number) r[3]).doubleValue()  // occupancy
                    );
                })
                .toList();
    }

}