package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;

import daewoo.team5.hotelreservation.domain.place.dto.ReservationStatsDTO;
import daewoo.team5.hotelreservation.domain.place.dto.ReviewableReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    // 예약 + 결제 조인 단건 조회

    Optional<Reservation> findTop1ByRoom_Place_IdAndGuest_IdOrderByCreatedAtDesc(Long placeId, Long guestId);

    // 기존 결제 포함 단건 조회
    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH Payment p ON p.reservation = r " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> findByIdWithPayments(@Param("reservationId") Long reservationId);

    // 소유자 단건 조회
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE r.reservationId = :reservationId " +
            "AND pl.owner.id = :ownerId")
    Optional<Reservation> findByIdAndOwnerId(@Param("reservationId") Long reservationId,
                                             @Param("ownerId") Long ownerId);

    // 소유자 전체 조회 (페이징)
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE pl.owner.id = :ownerId")

    Page<Reservation> findAllByOwnerId(@Param("ownerId") Long ownerId,
                                       Pageable pageable);

    Optional<Reservation> findByOrderId(String orderId);

   
    // 예약 ID로 예약을 조회
    Optional<Reservation> findById(Long reservationId);

    @Query("select r from Reservation r join fetch r.room join fetch r.guest where r.reservationId = :reservationId")
    Optional<Reservation> findByIdFetchJoin(Long reservationId);

    /**
     * 주석: 사용자가 특정 숙소에 대해 체크아웃 상태의 예약을 가지고 있는지 확인하는 쿼리
     * JPQL을 사용하여 Reservation -> RoomNo -> Room -> Places 엔티티를 순서대로 조인하여 확인합니다.
     * @param userId 사용자 ID
     * @param placeId 숙소 ID
     * @param status 확인할 예약 상태
     * @return 예약 존재 여부 (true/false)
     */
    /**
     * 주석: 사용자가 특정 숙소에 대해 체크아웃 상태의 예약을 가지고 있는지 확인하는 쿼리
     * JPQL을 사용하여 Reservation -> Room -> Places, Reservation -> Guest -> Users 엔티티를 순서대로 조인하여 확인합니다.
     *
     * @param userId  사용자 ID
     * @param placeId 숙소 ID
     * @param status  확인할 예약 상태
     * @return 예약 존재 여부 (true/false)
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Reservation r " +
            "JOIN r.room ro " +           // r.room으로 직접 조인
            "JOIN r.guest g " +            // guest와 조인
            "WHERE g.id = :userId " + // g.users.id로 사용자 ID에 접근
            "AND ro.place.id = :placeId " +
            "AND r.status = :status")
    boolean existsByUsersIdAndRoomPlaceIdAndStatus(@Param("userId") Long userId,
                                                   @Param("placeId") Long placeId,
                                                   @Param("status") Reservation.ReservationStatus status);

    // 오늘 생성된 예약
    @Query("SELECT COUNT(r) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE pl.owner.id = :ownerId " +
            "AND FUNCTION('DATE', r.createdAt) = :date")
    long countByOwnerIdAndCreatedDate(@Param("ownerId") Long ownerId,
                                      @Param("date") LocalDate date);

    // 오늘 체크인
    @Query("SELECT COUNT(r) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE pl.owner.id = :ownerId " +
            "AND r.resevStart = :date")
    long countByOwnerIdAndCheckIn(@Param("ownerId") Long ownerId,
                                  @Param("date") LocalDate date);

    // 오늘 체크아웃
    @Query("SELECT COUNT(r) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE pl.owner.id = :ownerId " +
            "AND r.resevEnd = :date")
    long countByOwnerIdAndCheckOut(@Param("ownerId") Long ownerId,
                                   @Param("date") LocalDate date);

    // 최근 6개월 월별 예약 현황 (네이티브 쿼리)
    @Query(value = "SELECT DATE_FORMAT(r.resev_start, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM reservations r " +
            "JOIN room rm ON r.room_id = rm.id " +
            "JOIN places pl ON rm.place_id = pl.id " +
            "WHERE pl.owner_id = :ownerId " +
            "AND r.status = 'confirmed' " +
            "AND r.payment_status = 'paid' " +
            "AND r.resev_start >= :sixMonthsAgo " +
            "GROUP BY DATE_FORMAT(r.resev_start, '%Y-%m') " +
            "ORDER BY DATE_FORMAT(r.resev_start, '%Y-%m')",
            nativeQuery = true)
    List<Object[]> findMonthlyConfirmedPaidReservations(@Param("ownerId") Long ownerId,
                                                        @Param("sixMonthsAgo") LocalDate sixMonthsAgo);
    /**
     * ✅ [추가] 특정 사용자가 특정 숙소에 대해 '리뷰 작성 가능한' 예약 목록을 조회하는 쿼리
     * 조건: 1. 체크아웃 상태일 것
     * 2. 아직 리뷰가 작성되지 않았을 것
     * @param guestId 사용자(게스트) ID
     * @param placeId 숙소 ID
     * @return 리뷰 작성 가능한 예약 목록
     */
    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.dto.ReviewableReservationResponse(r.reservationId, r.room.roomType, r.resevStart) " +
            "FROM Reservation r " +
            "WHERE r.guest.id = :guestId " +
            "AND r.room.place.id = :placeId " +
            "AND r.status = daewoo.team5.hotelreservation.domain.payment.entity.Reservation$ReservationStatus.checked_out " +
            "AND NOT EXISTS (SELECT rv FROM Review rv WHERE rv.reservation.reservationId = r.reservationId)")
    List<ReviewableReservationResponse> findReviewableReservations(@Param("guestId") Long guestId, @Param("placeId") Long placeId);

    @Query("SELECT COUNT(r) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place p " +
            "WHERE p.owner.id = :ownerId " +
            "AND YEAR(r.createdAt) = :year " +
            "AND MONTH(r.createdAt) = :month")
    long countByOwnerIdAndMonth(@Param("ownerId") Long ownerId,
                                @Param("year") int year,
                                @Param("month") int month);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place p " +
            "WHERE p.owner.id = :ownerId " +
            "AND YEAR(r.resevStart) = :year " +
            "AND MONTH(r.resevStart) = :month")
    long countTotalReservationsByOwnerAndMonth(@Param("ownerId") Long ownerId,
                                               @Param("year") int year,
                                               @Param("month") int month);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place p " +
            "WHERE p.owner.id = :ownerId " +
            "AND YEAR(r.resevStart) = :year " +
            "AND MONTH(r.resevStart) = :month " +
            "AND (r.status = 'cancelled' OR r.paymentStatus IN ('cancelled', 'refunded'))")
    long countCancelledOrRefundedReservationsByOwnerAndMonth(@Param("ownerId") Long ownerId,
                                                             @Param("year") int year,
                                                             @Param("month") int month);

    @Query("SELECT r.room.roomType, COUNT(r), SUM(r.finalAmount) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place p " +
            "WHERE p.owner.id = :ownerId " +
            "AND r.status = 'confirmed' " +
            "AND r.paymentStatus = 'paid' " +
            "AND r.resevStart BETWEEN :startDate AND :endDate " +
            "GROUP BY r.room.roomType")
    List<Object[]> findRoomRevenueByOwnerAndPeriod(@Param("ownerId") Long ownerId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}
