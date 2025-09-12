package daewoo.team5.hotelreservation.domain.users.repository;


import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsernameAndPassword(String username, String password);
    Optional<Users> findByUsername(String username);

    <T> Page<T> findAllBy(Class<T> type, Pageable pageable);

}
