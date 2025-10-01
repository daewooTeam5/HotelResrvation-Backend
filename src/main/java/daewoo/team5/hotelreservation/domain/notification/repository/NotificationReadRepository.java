package daewoo.team5.hotelreservation.domain.notification.repository;

import daewoo.team5.hotelreservation.domain.notification.entity.NotificationReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationReadRepository extends JpaRepository<NotificationReadEntity, Long> {
    Optional<NotificationReadEntity> findByUserId(Long userId);
    @Query(value = """
               select count(*)
                from notification_read nr
            right join notification n
            on n.user_id = nr.user_id
            where (n.user_id = :userId or n.user_id is null) AND (n.created_at > nr.read_at )
            """, nativeQuery = true)
    long findByUserIdReadCountWithNow(Long userId);

}
