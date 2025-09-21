package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {
    // 예약 ID로 예약을 조회
    Optional<Reservation> findById(Long reservationId);
    Optional<Reservation> findByOrderId(String orderId);

    @Query("select r from Reservation r join fetch r.room join fetch r.guest where r.reservationId = :reservationId")
    Optional<Reservation> findByIdFetchJoin(Long reservationId);
}