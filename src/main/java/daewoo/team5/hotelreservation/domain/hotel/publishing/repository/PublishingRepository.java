package daewoo.team5.hotelreservation.domain.hotel.publishing.repository;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishingRepository extends JpaRepository<Publishing, Long> {
    //JPA레파스토리 불러오면 기본적인 findAll같은 함수 다 끌어다 쓰기 가능
}
