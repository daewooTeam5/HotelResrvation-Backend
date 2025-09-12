package daewoo.team5.hotelreservation.domain.hotel.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 예약자 ID로 예약 조회
    List<Reservation> findByUserId(int userId);

    // 객실 ID로 예약 조회
    List<Reservation> findByRoomId(String roomId);

    // 예약 상태로 조회 (예: confirmed, pending 등)
    List<Reservation> findByStatus(String status);
}