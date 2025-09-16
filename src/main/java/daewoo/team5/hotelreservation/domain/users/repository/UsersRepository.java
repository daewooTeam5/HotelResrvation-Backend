package daewoo.team5.hotelreservation.domain.users.repository;


import daewoo.team5.hotelreservation.domain.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNameAndPassword(String username, String password);
    Optional<Users> findByName(String username);
    Optional<Users> findByEmail(String email);

    <T> Optional<T> findByName(String username, Class<T> type);
    <T> Optional<T> findById(Long id, Class<T> type);
    <T> Page<T> findAllBy(Class<T> type, Pageable pageable);

}
