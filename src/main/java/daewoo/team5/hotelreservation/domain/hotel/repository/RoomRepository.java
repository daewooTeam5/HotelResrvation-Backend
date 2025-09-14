package daewoo.team5.hotelreservation.domain.hotel.repository;

import daewoo.team5.hotelreservation.domain.hotel.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.status = :status WHERE r.id = :roomId")
    int updateRoomStatus(Long roomId, String status);
}
