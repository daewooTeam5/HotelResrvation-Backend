package daewoo.team5.hotelreservation.domain.users.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import io.lettuce.core.dynamic.annotation.Param;
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

    // ========== 신규 가입자 통계 ==========
    // 월별 신규 가입자
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m') as month, COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m')")
    List<Object[]> countMonthlyNewUsers();

    // 일별 신규 가입자
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d'), COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d')")
    List<Object[]> countDailyNewUsers();

    // 연별 신규 가입자
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y'), COUNT(u) " +
            "FROM Users u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y')")
    List<Object[]> countYearlyNewUsers();

    // ========== 탈퇴 관련 통계 추가 ==========
    // 총 탈퇴 회원 수
    @Query("SELECT COUNT(u) FROM Users u WHERE u.status = 'withdraw'")
    long countWithdrawnUsers();

    // 특정 날짜 이후 탈퇴 회원 수
    @Query("SELECT COUNT(u) FROM Users u WHERE u.status = 'withdraw' AND u.updatedAt >= :startDate")
    long countWithdrawnUsersSince(@Param("startDate") LocalDateTime startDate);

    // 일별 탈퇴 수
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m-%d'), COUNT(u) " +
            "FROM Users u WHERE u.status = 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m-%d')")
    List<Object[]> countDailyWithdrawnUsers();

    // 월별 탈퇴 수
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m'), COUNT(u) " +
            "FROM Users u WHERE u.status = 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m')")
    List<Object[]> countMonthlyWithdrawnUsers();

    // 연별 탈퇴 수
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y'), COUNT(u) " +
            "FROM Users u WHERE u.status = 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y')")
    List<Object[]> countYearlyWithdrawnUsers();

    @Query("""
    SELECT u, orq, f
    FROM Users u
    JOIN OwnerRequest orq ON orq.user = u
       AND orq.createdAt = (
            SELECT MAX(orq2.createdAt) 
            FROM OwnerRequest orq2 
            WHERE orq2.user = u
       )
    LEFT JOIN File f ON f.domain = 'owner_request' AND f.domainFileId = orq.id
""")
    List<Object[]> findAllUsersWithOwnerRequestAndFiles();

    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d') as date, " +
            "COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status != 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m-%d')")
    List<Object[]> countDailyTotalUsers();

    // 월별 누적 회원 수
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m') as month, " +
            "COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status != 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m')")
    List<Object[]> countMonthlyTotalUsers();

    // 연별 누적 회원 수
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%Y') as year, " +
            "COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status != 'withdraw' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y')")
    List<Object[]> countYearlyTotalUsers();

    @Query("SELECT u.role, COUNT(u) FROM Users u " +
            "WHERE u.role IN ('customer', 'hotel_owner') " +
            "GROUP BY u.role")
    List<Object[]> countByRole();

    // ===== 비활성 사용자 추이 (일별) =====
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m-%d'), COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status = 'inactive' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m-%d')")
    List<Object[]> countDailyInactiveUsers();

    // ===== 비활성 사용자 추이 (월별) =====
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m'), COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status = 'inactive' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y-%m')")
    List<Object[]> countMonthlyInactiveUsers();

    // ===== 비활성 사용자 추이 (연도별) =====
    @Query("SELECT FUNCTION('DATE_FORMAT', u.updatedAt, '%Y'), COUNT(u) " +
            "FROM Users u " +
            "WHERE u.status = 'inactive' " +
            "GROUP BY FUNCTION('DATE_FORMAT', u.updatedAt, '%Y')")
    List<Object[]> countYearlyInactiveUsers();

}