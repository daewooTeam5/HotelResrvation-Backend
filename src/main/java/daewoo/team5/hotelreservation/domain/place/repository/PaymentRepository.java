package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment.PaymentStatus;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentDetailProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentInfoProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentProjection;
import daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.projection.PaymentSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);

    Page<Payment> findAllByReservation_Guest_Id(Long guestId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :to " +
            "WHERE p.reservation.reservationId = :reservationId AND p.status = :from")
    int updateStatusByReservationId(Long reservationId, PaymentStatus from, PaymentStatus to);

    @Query("SELECT p FROM Payment p WHERE p.reservation.reservationId = :reservationId " +
            "ORDER BY p.transactionDate DESC")
    java.util.List<Payment> findAllByReservationIdOrderByTransactionDateDesc(Long reservationId);

    @Query("SELECT p FROM Places p WHERE p.owner.id = :ownerId")
    Optional<Places> findByOwnerId(@Param("ownerId") Long ownerId);

    Optional<Payment> findByOrderId(String orderId);
    // 예약 ID로 모든 결제 조회
    List<Payment> findByReservation_ReservationId(Long reservationId);

    // 예약 ID로 가장 최근 결제 1건만 조회
    Optional<Payment> findTop1ByReservation_ReservationIdOrderByTransactionDateDesc(Long reservationId);

    @Query(value = """
    SELECT COALESCE(SUM(p.amount), 0)
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND YEAR(p.transaction_date) = :year
      AND MONTH(p.transaction_date) = :month
    """, nativeQuery = true)
    long sumRevenueByOwnerAndMonth(
            @Param("ownerId") Long ownerId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query(value = """
    SELECT DATE_FORMAT(p.transaction_date, '%Y-%m') as month,
           COALESCE(SUM(p.amount), 0) as revenue
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date >= DATE_SUB(CURDATE(), INTERVAL :months MONTH)
    GROUP BY DATE_FORMAT(p.transaction_date, '%Y-%m')
    ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyRevenueLastMonths(
            @Param("ownerId") Long ownerId,
            @Param("months") int months
    );
    // 총 매출 합계|
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'paid'")
    long getTotalPayments();

    // 특정 기간 매출 합계
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'paid' AND p.transactionDate BETWEEN :start AND :end")
    long getPaymentsBetween(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('YEAR', p.transactionDate) AS year, " +
            "FUNCTION('MONTH', p.transactionDate) AS month, " +
            "COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p " +
            "WHERE p.status = 'paid' " +
            "GROUP BY FUNCTION('YEAR', p.transactionDate), FUNCTION('MONTH', p.transactionDate) " +
            "ORDER BY FUNCTION('YEAR', p.transactionDate), FUNCTION('MONTH', p.transactionDate)")
    List<Object[]> getMonthlyRevenue();

    @Query("SELECT pl.name, SUM(p.amount) " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE p.status = 'paid' " +
            "GROUP BY pl.name " +
            "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getTop5HotelsByRevenue();

    @Query("SELECT pl.name, COUNT(r) " +
            "FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "GROUP BY pl.name " +
            "ORDER BY COUNT(r) DESC")
    List<Object[]> getTop5HotelsByReservations();

    // 결제 요약 Projection: 게스트 기준 + 숙소 첫 번째 이미지
    @Query(value = """
        SELECT 
            p.id                            AS paymentId,
            p.payment_key                   AS paymentKey,
            p.order_id                      AS orderId,
            p.status                        AS status,
            p.method                        AS method,
            p.amount                        AS amount,
            p.transaction_date              AS transactionDate,

            r.reservation_id                AS reservationId,
            r.resev_start                   AS resevStart,
            r.resev_end                     AS resevEnd,

            g.id                            AS guestId,
            g.first_name                    AS guestFirstName,
            g.last_name                     AS guestLastName,

            pl.id                           AS placeId,
            pl.name                         AS placeName,

            rm.id                           AS roomId,
            rm.room_type                    AS roomType,

            img.image_url                   AS firstImageUrl
        FROM payments p
        JOIN reservations r ON p.reservation_id = r.reservation_id
        JOIN guest g         ON r.user_id = g.id
        JOIN room rm         ON r.room_id = rm.id
        JOIN places pl       ON rm.place_id = pl.id
        LEFT JOIN (
            SELECT f.domain_file_id AS room_id, f.url AS image_url
            FROM file f
            JOIN (
                SELECT domain_file_id, MIN(id) AS min_id
                FROM file
                WHERE domain = 'room' AND filetype = 'image'
                GROUP BY domain_file_id
            ) first_file ON first_file.domain_file_id = f.domain_file_id AND f.id = first_file.min_id
            WHERE f.domain = 'room' AND f.filetype = 'image'
        ) img ON img.room_id = rm.id
        WHERE g.id = :guestId
        ORDER BY p.transaction_date DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM payments p
        JOIN reservations r ON p.reservation_id = r.reservation_id
        JOIN guest g         ON r.user_id = g.id
        WHERE g.id = :guestId
    """,
            nativeQuery = true)
    Page<PaymentSummaryProjection> findPaymentSummariesByGuestId(@Param("guestId") Long guestId, Pageable pageable);

    List<PaymentInfoProjection> findByReservation_Room_Place_Id(Long placeId);
    // ✅ 일별 매출
    @Query(value = """
    SELECT DATE(p.transaction_date) AS label, COALESCE(SUM(p.amount), 0) as revenue
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date BETWEEN :startDate AND :endDate
    GROUP BY DATE(p.transaction_date)
    ORDER BY DATE(p.transaction_date)
    """, nativeQuery = true)
    List<Object[]> findDailyRevenue(@Param("ownerId") Long ownerId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    // ✅ 주별 매출
    @Query(value = """
    SELECT YEARWEEK(p.transaction_date, 1) AS label, COALESCE(SUM(p.amount), 0) as revenue
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date BETWEEN :startDate AND :endDate
    GROUP BY YEARWEEK(p.transaction_date, 1)
    ORDER BY YEARWEEK(p.transaction_date, 1)
    """, nativeQuery = true)
    List<Object[]> findWeeklyRevenue(@Param("ownerId") Long ownerId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // ✅ 월별 매출
    @Query(value = """
    SELECT DATE_FORMAT(p.transaction_date, '%Y-%m') AS label, COALESCE(SUM(p.amount), 0) as revenue
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date BETWEEN :startDate AND :endDate
    GROUP BY DATE_FORMAT(p.transaction_date, '%Y-%m')
    ORDER BY DATE_FORMAT(p.transaction_date, '%Y-%m')
    """, nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("ownerId") Long ownerId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // ✅ 연도별 매출
    @Query(value = """
    SELECT YEAR(p.transaction_date) AS label, COALESCE(SUM(p.amount), 0) as revenue
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date BETWEEN :startDate AND :endDate
    GROUP BY YEAR(p.transaction_date)
    ORDER BY YEAR(p.transaction_date)
    """, nativeQuery = true)
    List<Object[]> findYearlyRevenue(@Param("ownerId") Long ownerId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // ✅ 결제 수단별 매출/건수 통계
    @Query(value = """
    SELECT p.method AS method,
           COUNT(*) AS count,
           COALESCE(SUM(p.amount), 0) AS totalAmount
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.reservation_id
    JOIN room rm ON r.room_id = rm.id
    JOIN places pl ON rm.place_id = pl.id
    WHERE pl.owner_id = :ownerId
      AND p.status = 'paid'
      AND p.transaction_date BETWEEN :startDate AND :endDate
    GROUP BY p.method
    """, nativeQuery = true)
    List<Object[]> findPaymentMethodStats(@Param("ownerId") Long ownerId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    @Query("""
    select 
        p.id                             as paymentId,
        p.paymentKey                     as paymentKey,
        p.orderId                        as orderId,
        p.status                         as status,
        p.method                         as method,
        p.amount                         as amount,
        p.transactionDate                as transactionDate,

        r.reservationId                  as reservationId,
        r.resevStart                     as resevStart,
        r.resevEnd                       as resevEnd,
        r.baseAmount                     as baseAmount,
        r.finalAmount                    as finalAmount,
        r.fixedDiscountAmount            as fixedDiscountAmount,
        r.couponDiscountAmount           as couponDiscountAmount,
        r.pointDiscountAmount            as pointDiscountAmount,
        r.request                        as request,

        pl.id                            as placeId,
        pl.name                          as placeName,
        pl.checkIn                       as checkIn,

        rm.id                            as roomId,
        rm.roomType                      as roomType,
        rm.price                         as roomPrice,

        f.url                            as firstImageUrl,

        ch.id                            as couponHistoryId,
        ch.discountAmount               as discountAmount,
        ch.usedAt                       as usedAt,
        ch.status                        as couponStatus,
        c.id                             as couponId,
        c.couponName                     as couponName,
        c.amount                         as couponAmount,
        c.couponType                     as couponType,
        c.couponCode                     as couponCode,
        c.createdAt                      as couponCreatedAt,
        c.expiredAt                      as couponExpiredAt,
        c.minOrderAmount                 as minOrderAmount,
        c.maxOrderAmount                 as maxOrderAmount

    from Payment p
    join p.reservation r
    join r.room rm
    join rm.place pl
    left join File f on (
        f.domain = 'room' and f.filetype = 'image' and f.domainFileId = rm.id
        and f.id = (
            select min(f2.id)
            from File f2
            where f2.domain = 'room' and f2.filetype = 'image' and f2.domainFileId = rm.id
        )
    )
    left join CouponHistory ch on ch.reservation = r
    left join ch.userCoupon uc
    left join uc.coupon c
    where p.id = :paymentId
""")
    Optional<PaymentDetailProjection> findPaymentDetailById(@Param("paymentId") Long paymentId);

    // 사용자별 결제 내역 조회 (Projection)
    @Query("SELECT p.id as id, p.orderId as orderId, p.paymentKey as paymentKey, " +
            "p.amount as amount, p.method as method, p.status as status, " +
            "p.transactionDate as transactionDate, p.methodType as methodType, " +
            "r.reservationId as reservation_reservationId " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "WHERE r.guest.id = :userId")
    List<PaymentProjection> findPaymentsByUserId(Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.transactionDate BETWEEN :start AND :end")
    Long getRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 일별 매출
    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse(" +
            "CAST(DATE(p.transactionDate) AS string), SUM(p.amount)) " +
            "FROM Payment p " +
            "WHERE p.transactionDate BETWEEN :start AND :end " +
            "GROUP BY DATE(p.transactionDate) " +
            "ORDER BY DATE(p.transactionDate)")
    List<ChartDataResponse> getDailyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 월별 매출
    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse(" +
            "CONCAT(CAST(YEAR(p.transactionDate) AS string), '-', LPAD(CAST(MONTH(p.transactionDate) AS string), 2, '0')), " +
            "SUM(p.amount)) " +
            "FROM Payment p " +
            "GROUP BY YEAR(p.transactionDate), MONTH(p.transactionDate) " +
            "ORDER BY YEAR(p.transactionDate), MONTH(p.transactionDate)")
    List<ChartDataResponse> getMonthRevenue();

    // 연도별 매출
    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse(CAST(YEAR(p.transactionDate) AS string), SUM(p.amount)) " +
            "FROM Payment p " +
            "GROUP BY YEAR(p.transactionDate) " +
            "ORDER BY YEAR(p.transactionDate)")
    List<ChartDataResponse> getYearlyRevenue();

    // 카테고리별 매출
    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.dto.ChartDataResponse(pc.name, SUM(p.amount)) " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "JOIN r.room ro " +
            "JOIN ro.place pl " +
            "JOIN pl.category pc " +
            "GROUP BY pc.name " +
            "ORDER BY SUM(p.amount) DESC")
    List<ChartDataResponse> getRevenueByCategory();

    @Query("SELECT r.guest.id, SUM(p.amount) " +
            "FROM Payment p JOIN p.reservation r " +
            "GROUP BY r.guest.id ORDER BY SUM(p.amount) DESC")
    List<Object[]> findTopCustomersByPayments();

    @Query(value = """
    SELECT COALESCE(COUNT(*), 0)
    FROM (
        SELECT r.user_id
        FROM payments p
        JOIN reservations r ON p.reservation_id = r.reservation_id
        GROUP BY r.user_id
        HAVING COUNT(p.id) >= 2
    ) sub
""", nativeQuery = true)
    long countRepeatUsers();

    // 네이티브 쿼리 → reservations.user_id 기준으로 수정
    @Query(value = "SELECT AVG(t.sumAmt) " +
            "FROM (SELECT SUM(p.amount) AS sumAmt " +
            "      FROM payments p " +
            "      JOIN reservations r ON p.reservation_id = r.reservation_id " +
            "      GROUP BY r.user_id) t", nativeQuery = true)
    Double findAvgPaymentsPerCustomer();

    @Query("SELECT FUNCTION('DATE_FORMAT', p.transactionDate, '%Y-%m-%d'), SUM(p.amount) " +
            "FROM Payment p GROUP BY FUNCTION('DATE_FORMAT', p.transactionDate, '%Y-%m-%d')")
    List<Object[]> sumDailyPayments();

    @Query("SELECT FUNCTION('DATE_FORMAT', p.transactionDate, '%Y-%m'), SUM(p.amount) " +
            "FROM Payment p GROUP BY FUNCTION('DATE_FORMAT', p.transactionDate, '%Y-%m')")
    List<Object[]> sumMonthlyPayments();

    @Query("SELECT FUNCTION('DATE_FORMAT', p.transactionDate, '%Y'), SUM(p.amount) " +
            "FROM Payment p GROUP BY FUNCTION('DATE_FORMAT', p.transactionDate, '%Y')")
    List<Object[]> sumYearlyPayments();


}
