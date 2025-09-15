package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE room r SET r.status = :status WHERE r.id = :roomId")
    int updateRoomStatus(Long roomId, String status);
}
