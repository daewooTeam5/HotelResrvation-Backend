package daewoo.team5.hotelreservation.domain.users.repository;


import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNameAndPassword(String username, String password);
    Optional<Users> findByName(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUserId(String userId);
    Optional<UserProjection> findProjectedById(Long id);
    <T> Optional<T> findByName(String username, Class<T> type);
    <T> Optional<T> findById(Long id, Class<T> type);
    <T> Page<T> findAllBy(Class<T> type, Pageable pageable);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.createdAt >= :startOfMonth")
    long countNewUsers(LocalDateTime startOfMonth);

    @Query("SELECT u.id FROM Users u WHERE u.createdAt >= :fromDate")
    List<Long> findNewUsers(LocalDateTime fromDate);

    @Query("SELECT u.id FROM Users u WHERE NOT EXISTS (" +
            "SELECT r FROM Reservation r WHERE r.guest.id = u.id AND r.resevStart >= :sinceDate)")
    List<Long> findDormantUsers(LocalDate sinceDate);

    // 월별 신규 가입자
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m') as month, COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m')")
    List<Object[]> countMonthlyNewUsers();

    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d'), COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d')")
    List<Object[]> countDailyNewUsers();

    // 연별
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y'), COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y')")
    List<Object[]> countYearlyNewUsers();
}
