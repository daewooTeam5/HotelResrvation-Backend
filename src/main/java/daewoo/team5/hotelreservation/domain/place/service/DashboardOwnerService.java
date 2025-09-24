package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardOwnerService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;
    private final PlaceRepository placeRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    // 오늘 현황
    public ReservationStatsDTO getTodayStats(Long ownerId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        long todayReservations = reservationRepository.countByOwnerIdAndCreatedDate(ownerId, today);
        long todayCheckIn = reservationRepository.countByOwnerIdAndCheckIn(ownerId, today);
        long todayCheckOut = reservationRepository.countByOwnerIdAndCheckOut(ownerId, today);
        long yesterdayReservations = reservationRepository.countByOwnerIdAndCreatedDate(ownerId, yesterday);

        double growthRate = yesterdayReservations > 0
                ? ((double) (todayReservations - yesterdayReservations) / yesterdayReservations) * 100
                : (todayReservations > 0 ? 100.0 : 0.0);

        return new ReservationStatsDTO(todayReservations, todayCheckIn, todayCheckOut, growthRate);
    }

    // 최근 6개월 월별 현황
    public List<ReservationStatsDTO> getMonthlyStats(Long ownerId) {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        List<Object[]> rawResults = reservationRepository.findMonthlyConfirmedPaidReservations(ownerId, sixMonthsAgo);

        return rawResults.stream()
                .map(row -> new ReservationStatsDTO((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    // 오늘 기준 점유율
    public OccupancyRateDTO getOccupancyRate(Long ownerId) {
        LocalDate today = LocalDate.now();

        List<Room> rooms = roomRepository.findAllByOwnerId(ownerId);

        int totalRooms = 0;
        int usedRooms = 0;

        for (Room room : rooms) {
            int capacity = room.getCapacityRoom(); // 총 객실 수
            int available = dailyPlaceReservationRepository
                    .findByRoomIdAndDate(room.getId(), today)
                    .map(d -> d.getAvailableRoom())
                    .orElse(capacity); // 데이터 없으면 full available 가정

            totalRooms += capacity;
            usedRooms += (capacity - available);
        }

        double rate = totalRooms > 0 ? (double) usedRooms / totalRooms * 100 : 0.0;

        return new OccupancyRateDTO(usedRooms, totalRooms, rate);
    }

    // 평균 평점
    public RatingStatsDTO getRatingStats(Long ownerId) {
        return placeRepository.findByOwnerId(ownerId)
                .map(place -> new RatingStatsDTO(
                        place.getAvgRating() != null ? place.getAvgRating().doubleValue() : 0.0,
                        place.getReviewCount() != null ? place.getReviewCount() : 0
                ))
                .orElse(new RatingStatsDTO(0.0, 0));
    }

    // 이번 달 매출
    public MonthlyRevenueDTO getMonthlyRevenue(Long ownerId) {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        long thisRevenue = paymentRepository.sumRevenueByOwnerAndMonth(
                ownerId, thisMonth.getYear(), thisMonth.getMonthValue()
        );
        long lastRevenue = paymentRepository.sumRevenueByOwnerAndMonth(
                ownerId, lastMonth.getYear(), lastMonth.getMonthValue()
        );

        double growthRate = lastRevenue > 0
                ? ((double)(thisRevenue - lastRevenue) / lastRevenue) * 100
                : (thisRevenue > 0 ? 100.0 : 0.0);

        return new MonthlyRevenueDTO(thisRevenue, lastRevenue, growthRate);
    }

    public List<MonthlyRevenueChartDTO> getMonthlyRevenueChart(Long ownerId, int months) {
        List<Object[]> raw = paymentRepository.findMonthlyRevenueLastMonths(ownerId, months);

        return raw.stream()
                .map(row -> new MonthlyRevenueChartDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // 최근 리뷰 조회
    public List<RecentReviewDTO> getRecentReviews(Long ownerId) {
        List<Review> reviews = reviewRepository.findTop3ByPlace_OwnerIdOrderByCreatedAtDesc(ownerId);

        return reviews.stream()
                .map(r -> new RecentReviewDTO(
                        r.getReviewId(),
                        r.getUser().getName(), // users.name
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .toList();
    }

}
