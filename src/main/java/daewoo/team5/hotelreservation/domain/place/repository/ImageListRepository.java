package daewoo.team5.hotelreservation.domain.place.repository; // 경로는 프로젝트 구조에 맞게 조정하세요.

import daewoo.team5.hotelreservation.domain.place.entity.ImageList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;

@Repository
public interface ImageListRepository extends JpaRepository<ImageList, Long> {
    List<ImageList> findByPlaceId(Long placeId);



}