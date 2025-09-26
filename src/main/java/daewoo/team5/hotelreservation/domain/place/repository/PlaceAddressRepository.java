package daewoo.team5.hotelreservation.domain.place.repository;


import daewoo.team5.hotelreservation.domain.place.entity.PlaceAddress;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PlaceAddressRepository extends JpaRepository<PlaceAddress, Long> {
    Optional<PlaceAddress> findByPlace_Id(Long placeId);
}
