package daewoo.team5.hotelreservation.domain.place.repository;


import daewoo.team5.hotelreservation.domain.place.entity.PlaceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceAddressRepository extends JpaRepository<PlaceAddress, Integer> {
}
