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
    // 예약 ID로 예약을 조회
    Optional<Reservation> findById(Long reservationId);
    /**
     * 주석: 사용자가 특정 숙소에 대해 체크아웃 상태의 예약을 가지고 있는지 확인하는 쿼리
     * JPQL을 사용하여 Reservation -> RoomNo -> Room -> Places 엔티티를 순서대로 조인하여 확인합니다.
     * @param userId 사용자 ID
     * @param placeId 숙소 ID
     * @param status 확인할 예약 상태
     * @return 예약 존재 여부 (true/false)
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Reservation r " +
            "JOIN r.roomNo rn " +
            "JOIN rn.room ro " +
            "WHERE r.users.id = :userId " +
            "AND ro.place.id = :placeId " +
            "AND r.status = :status")
    boolean existsByUsersIdAndRoomPlaceIdAndStatus(@Param("userId") Long userId,
                                                   @Param("placeId") Long placeId,
                                                   @Param("status") Reservation.ReservationStatus status);
}