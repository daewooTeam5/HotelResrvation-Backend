package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :to " +
            "WHERE p.reservation.reservationId = :reservationId AND p.status = :from")
    int updateStatusByReservationId(Long reservationId, PaymentStatus from, PaymentStatus to);

    @Query("SELECT p FROM Payment p WHERE p.reservation.reservationId = :reservationId " +
            "ORDER BY p.transactionDate DESC")
    java.util.List<Payment> findAllByReservationIdOrderByTransactionDateDesc(Long reservationId);
}