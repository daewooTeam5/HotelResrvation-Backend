package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.PlaceAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceAmenityRepository extends JpaRepository<PlaceAmenity, Long> {
    // 특정 호텔(Place)의 편의시설 모두 삭제
    void deleteByPlaceId(Long placeId);

    // 특정 호텔(Place)의 편의시설 조회
    List<PlaceAmenity> findByPlaceId(Long placeId);
}
