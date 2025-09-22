package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {
    Optional<GuestEntity> findByEmailAndFirstNameAndLastName(String email, String firstName, String lastName);
    Optional<GuestEntity> findByUsersId(Long usersId);
}
