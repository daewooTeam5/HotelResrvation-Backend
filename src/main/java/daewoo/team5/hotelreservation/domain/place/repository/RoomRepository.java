package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // ownerId로 해당 소유자의 모든 객실 유형 조회
    @Query("SELECT r FROM room r " +
            "JOIN r.place p " +
            "WHERE p.owner.id = :ownerId")
    List<Room> findAllByOwnerId(@Param("ownerId") Long ownerId);

    // ownerId + roomId 단건 조회 (권한 체크용)
    @Query("SELECT r FROM room r " +
            "JOIN r.place p " +
            "WHERE r.id = :roomId " +
            "AND p.owner.id = :ownerId")
    Optional<Room> findByIdAndOwnerId(@Param("roomId") Long roomId,
                                      @Param("ownerId") Long ownerId);
}
