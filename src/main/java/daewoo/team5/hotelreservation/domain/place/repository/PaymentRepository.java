package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment.PaymentStatus;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

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
}