package daewoo.team5.hotelreservation.domain.place.repository;


import aj.org.objectweb.asm.commons.Remapper;
import daewoo.team5.hotelreservation.domain.place.entity.PlaceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceAddressRepository extends JpaRepository<PlaceAddress, Long> {
    List<PlaceAddress> findByPlaceId(Long placeId);


    void deleteByPlaceId(Long placeId);//삭제용

    Optional<PlaceAddress> findFirstByPlaceId(Long placeId);
}
