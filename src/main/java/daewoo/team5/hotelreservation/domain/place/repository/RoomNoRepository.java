package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.RoomNo;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface RoomNoRepository extends JpaRepository<RoomNo, Long> {
    Optional<RoomNo> findByRoomNo(String roomNo);
}
