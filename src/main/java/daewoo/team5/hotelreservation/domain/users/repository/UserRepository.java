package daewoo.team5.hotelreservation.domain.users.repository;

import daewoo.team5.hotelreservation.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
