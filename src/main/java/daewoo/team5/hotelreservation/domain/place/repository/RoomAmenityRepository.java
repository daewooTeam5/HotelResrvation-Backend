package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.RoomAmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomAmenityRepository extends JpaRepository<RoomAmenityEntity, Long> {
    // 특정 객실(Room)의 편의시설 삭제
    void deleteByRoomId(Long roomId);

    // 여러 객실 편의시설 한번에 삭제
    void deleteByRoomIdIn(List<Long> roomIds);

    // 특정 객실(Room)의 편의시설 조회
    List<RoomAmenityEntity> findByRoomId(Long roomId);
}
