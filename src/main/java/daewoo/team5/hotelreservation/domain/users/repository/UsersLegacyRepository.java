package daewoo.team5.hotelreservation.domain.users.repository;


import daewoo.team5.hotelreservation.domain.users.entity.UsersLegacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersLegacyRepository extends JpaRepository<UsersLegacy, Long> {
    Optional<UsersLegacy> findByUsernameAndPassword(String username, String password);
    Optional<UsersLegacy> findByUsername(String username);

    <T> Page<T> findAllBy(Class<T> type, Pageable pageable);

}
