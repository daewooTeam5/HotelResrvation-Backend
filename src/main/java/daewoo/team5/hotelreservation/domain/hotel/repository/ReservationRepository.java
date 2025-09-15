package daewoo.team5.hotelreservation.domain.hotel.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {
    // 예약 ID로 예약을 조회
    Optional<Reservation> findById(Long reservationId);
}