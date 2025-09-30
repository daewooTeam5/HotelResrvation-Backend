package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmentiesRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findAllByType(Amenity.Type type);
}//이거를 findByAll로 호출하면 그 카테고리에 맞는 호텔들이 나오게끔 한단건가
