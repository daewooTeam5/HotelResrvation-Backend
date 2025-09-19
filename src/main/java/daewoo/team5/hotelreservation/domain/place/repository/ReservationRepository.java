package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    // 기존 결제 포함 단건 조회
    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH Payment p ON p.reservation = r " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> findByIdWithPayments(@Param("reservationId") Long reservationId);

    // 소유자 ID 기반 단건 조회
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE r.reservationId = :reservationId " +
            "AND pl.owner.id = :ownerId")
    Optional<Reservation> findByIdAndOwnerId(
            @Param("reservationId") Long reservationId,
            @Param("ownerId") Long ownerId);

    // 소유자 ID 기반 전체 조회
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room rm " +
            "JOIN rm.place pl " +
            "WHERE pl.owner.id = :ownerId")
    org.springframework.data.domain.Page<Reservation> findAllByOwnerId(
            @Param("ownerId") Long ownerId,
            org.springframework.data.domain.Pageable pageable);
}
