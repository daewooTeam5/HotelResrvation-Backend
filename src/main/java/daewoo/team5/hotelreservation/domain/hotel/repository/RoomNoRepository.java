package daewoo.team5.hotelreservation.domain.hotel.repository;

import daewoo.team5.hotelreservation.domain.hotel.entity.RoomNo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomNoRepository extends JpaRepository<RoomNo, Long> {
    Optional<RoomNo> findByRoomNo(String roomNo);
}
